package com.totoro.datasync.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.totoro.datasync.CursorType;
import com.totoro.datasync.annotation.SyncColumn;
import com.totoro.datasync.annotation.SyncForeignColumn;
import com.totoro.datasync.annotation.SyncTable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class SyncAnnotationProcessor extends AbstractProcessor {

    private Elements elementUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtil = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SyncTable.class);

        Map<String, EntityInfo> infoMap = new HashMap<String, EntityInfo>();
        EntityInfo entityInfo;

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            List<? extends Element> members = elementUtil.getAllMembers(typeElement);

            SyncTable syncTable = element.getAnnotation(SyncTable.class);
            entityInfo = new EntityInfo((TypeElement) element, syncTable);

            // Fetch annotations on members and put them to map
            for (Element item : members) {
                SyncColumn column = item.getAnnotation(SyncColumn.class);
                if (column != null) {
                    entityInfo.addSyncColumn(column);
                }
                SyncForeignColumn foreignColumn = item.getAnnotation(SyncForeignColumn.class);
                if (foreignColumn != null) {
                    entityInfo.addSyncForeignColumn(foreignColumn);
                }
            }
            infoMap.put(syncTable.name(), entityInfo);
        }

        for (String tableName : infoMap.keySet()) {
            EntityInfo info = infoMap.get(tableName);

            MethodSpec insertConfigSQL = buildInsertConfigSql(info);
            MethodSpec fetchSQl = buildFetch(info);
            MethodSpec assembleData = buildAssembleData(info, infoMap);
            MethodSpec insertSQL = buildInsertData(info);

            TypeSpec sqlCreator = TypeSpec.classBuilder(info.element.getSimpleName() + "SyncUtil")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(insertConfigSQL)
                    .addMethod(fetchSQl)
                    .addMethod(assembleData)
                    .addMethod(insertSQL)
                    .build();

            JavaFile javaFile = JavaFile.builder(getPackageName(info.element), sqlCreator).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SyncTable.class.getCanonicalName());
    }

    private String getPackageName(TypeElement type) {
        return elementUtil.getPackageOf(type).getQualifiedName().toString();
    }

    private MethodSpec buildInsertConfigSql(EntityInfo info) {
        StringBuilder insertConfig = new StringBuilder("INSERT OR REPLACE INTO sync_config (name,syncname,type,sequence,uploadtime,downloadtime,userid) VALUES ");
        insertConfig.append("('").append(info.syncTable.name()).append("',");
        insertConfig.append("'").append(info.syncTable.syncName()).append("',");
        insertConfig.append(info.syncTable.type()).append(",");
        insertConfig.append(info.syncTable.sequence()).append(",");
        insertConfig.append(-28800000).append(",");
        insertConfig.append(-28800000).append(",");
        insertConfig.append("'\" + id + \"')");

        return MethodSpec.methodBuilder("insertConfigSQL")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "id")
                .returns(String.class)
                .addStatement("return \"" + insertConfig.toString() + "\"")
                .build();
    }

    private MethodSpec buildFetch(EntityInfo info) {
        StringBuilder fetchSQL = new StringBuilder();
        StringBuilder where = new StringBuilder();
        StringBuilder fromTables = new StringBuilder(info.syncTable.name());

        if (info.syncTable.type() != SyncTable.TYPE_DOWNLOAD) {
            fetchSQL.append("SELECT ").append(info.syncTable.name()).append(".*");

            if (info.syncForeignColumns.size() > 0) {
                for (SyncForeignColumn foreignColumn : info.syncForeignColumns) {
                    String foreignTable = foreignColumn.table();
                    String[] foreignColumns = foreignColumn.foreignColumns();

                    for (String col : foreignColumns) {
                        fetchSQL.append(",").append(foreignTable).append(".").append(col);
                    }

                    fromTables.append(",").append(foreignTable);
                    where.append(info.syncTable.name()).append(".").append(foreignColumn.name()).append("=")
                            .append(foreignTable).append(".").append(foreignColumn.foreignKey()).append(" AND ");
                }
                where.delete(where.length() - 5, where.length());
            }
            fetchSQL.append(" FROM ").append(fromTables);

            if (where.length() > 0) {
                fetchSQL.append(" WHERE ").append(where);
            }
        } else {
            fetchSQL.append("\"\"");
        }

        ClassName textUtil = ClassName.get("android.text", "TextUtils");

        return MethodSpec.methodBuilder("fetchSQL")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "where")
                .addParameter(Object[].class, "args")
                .addStatement("StringBuilder sql = new StringBuilder(\"" + fetchSQL + "\")")
                .beginControlFlow("if (!$T.isEmpty(where))", textUtil)
                .beginControlFlow("if (sql.indexOf(\"WHERE\") == -1)")
                .addStatement("sql.append(\" WHERE \")")
                .nextControlFlow("else")
                .addStatement("sql.append(\" AND \")")
                .endControlFlow()
                .addStatement("sql.append(where)")
                .endControlFlow()
                .addStatement("return String.format(sql.toString(), args)")
                .returns(String.class)
                .build();
    }

    private MethodSpec buildAssembleData(EntityInfo info, Map<String, EntityInfo> infoMap) {
        ClassName cursor = ClassName.get("android.database", "Cursor");
        ClassName jsonArray = ClassName.get("org.json", "JSONArray");
        ClassName jsonObject = ClassName.get("org.json", "JSONObject");
        ClassName jsonException = ClassName.get("org.json", "JSONException");

        MethodSpec.Builder builder = MethodSpec.methodBuilder("assembleData")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(cursor, "c")
                .addStatement("$T array = new $T()", jsonArray, jsonArray)
                .beginControlFlow("if (c != null)")
                .addStatement("$T obj", jsonObject)
                .beginControlFlow("while (c.moveToNext())")
                .addStatement("obj = new $T()", jsonObject)
                .beginControlFlow("try");

        for (SyncColumn syncColumn : info.syncColumns.values()) {
            builder.addStatement("obj.put(\"" + syncColumn.syncName() + "\", " +
                "c." + getMethodName(syncColumn.rawType()) + "(c.getColumnIndex(\"" + syncColumn.name() + "\")))");
        }

        for (SyncForeignColumn foreignColumn : info.syncForeignColumns) {
            EntityInfo foreignInfo = infoMap.get(foreignColumn.table());
            if (foreignInfo != null) {
                for (String col : foreignColumn.foreignColumns()) {
                    SyncColumn syncColumn = foreignInfo.syncColumns.get(col);
                    builder.addStatement("obj.put(\"" + syncColumn.syncName() + "\", " +
                        "c." + getMethodName(syncColumn.rawType()) + "(c.getColumnIndex(\"" +
                           foreignInfo.syncTable.name() + "." + syncColumn.name() + "\")))");
                }
            }
        }

        builder.nextControlFlow("catch ($T e)", jsonException)
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .addStatement("array.put(obj)")
                .endControlFlow()
                .addStatement("c.close()")
                .endControlFlow()
                .addStatement("return array.toString()")
                .returns(String.class);

        return builder.build();
    }

    private MethodSpec buildInsertData(EntityInfo info) {
        ClassName jsonObject = ClassName.get("org.json", "JSONObject");

        MethodSpec.Builder builder = MethodSpec.methodBuilder("insertSQL")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(jsonObject, "obj")
                .addStatement("StringBuilder sql = new StringBuilder()")
                .addStatement("StringBuilder columns = new StringBuilder(\"(\")")
                .addStatement("StringBuilder values = new StringBuilder(\"(\")")
                .addStatement("sql.append(\"INSERT OR REPLACE INTO " + info.syncTable.name() + " \")");

        List<SyncColumn> columns = new ArrayList<SyncColumn>(info.syncColumns.values());

        for (int i = 0; i < columns.size(); i++) {
            appendColumnStatement(builder, columns.get(i).name(),
                    i == columns.size() - 1 && info.syncForeignColumns.size() == 0);
        }

        for (int i = 0; i < info.syncForeignColumns.size(); i++) {
            appendColumnStatement(builder, info.syncForeignColumns.get(i).name(), i == info.syncForeignColumns.size() - 1);
        }

        for (int i = 0; i < columns.size(); i++) {
            appendValueStatement(builder, columns.get(i).syncName(), columns.get(i).rawType(),
                    i == columns.size() - 1 && info.syncForeignColumns.size() == 0);
        }

        for (int i = 0; i < info.syncForeignColumns.size(); i++) {
            appendValueStatement(builder, info.syncForeignColumns.get(i).syncName(),
                    info.syncForeignColumns.get(i).rawType(), i == info.syncForeignColumns.size() - 1);
        }

        builder.addStatement("sql.append(columns).append(\" VALUES \").append(values)")
                .addStatement("return sql.toString()")
                .returns(String.class);

        return builder.build();
    }

    private String stringSemi(int type) {
        return type == CursorType.FIELD_TYPE_STRING ? "'" : "";
    }

    private String getMethodName(int type) {
        switch (type) {
            case CursorType.FIELD_TYPE_BLOB:
                return "getBlob";
            case CursorType.FIELD_TYPE_FLOAT:
                return "getFloat";
            case CursorType.FIELD_TYPE_INTEGER:
                return "getInt";
            case CursorType.FIELD_TYPE_LONG:
                return "getLong";
            case CursorType.FIELD_TYPE_STRING:
                return "getString";
            default:
                return "";
        }
    }

    private String getJSONObjMethodName(int type) {
        switch (type) {
            case CursorType.FIELD_TYPE_FLOAT:
                return "optDouble";
            case CursorType.FIELD_TYPE_INTEGER:
                return "optInt";
            case CursorType.FIELD_TYPE_LONG:
                return "optLong";
            case CursorType.FIELD_TYPE_STRING:
                return "optString";
            default:
                return "";
        }
    }

    private void appendColumnStatement(MethodSpec.Builder builder, String name, boolean last) {
        builder.addStatement("columns.append(\"" + name + (last ? ")" : ",") + "\")");
    }

    private void appendValueStatement(MethodSpec.Builder builder, String syncName, int type, boolean last) {
        builder.addStatement("values.append(\"" + stringSemi(type) +"\" + obj." +
                getJSONObjMethodName(type) + "(\"" + syncName + "\") + \"" +
                stringSemi(type) + (last ? ")" : ",") + "\")");
    }
}
