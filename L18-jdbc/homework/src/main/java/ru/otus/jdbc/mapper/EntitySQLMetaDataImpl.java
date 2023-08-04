package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData metaData;
    private final String fieldsList;
    private final String fieldsListWoId;

    public EntitySQLMetaDataImpl(EntityClassMetaData metaData) {
        this.metaData = metaData;
        List<Field> fields = metaData.getAllFields();
        this.fieldsList = fields.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        List<Field> fieldsWoId = metaData.getFieldsWithoutId();
        this.fieldsListWoId = fieldsWoId.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String getSelectAllSql() {
        return "select " + fieldsList + " from " + metaData.getName();
    }

    @Override
    public String getSelectByIdSql() {
        return  "select " + fieldsList + " from " + metaData.getName() + " where " + metaData.getIdField().getName() + " = ?";
    }

    @Override
    public String getInsertSql() {
        return "insert into " + metaData.getName() + "("+fieldsListWoId+") values(" + fieldsListWoId.replaceAll("[^,]", "").replace(",","?,").concat("?") +")";
    }

    @Override
    public String getUpdateSql() {
        List<Field> fields = metaData.getFieldsWithoutId();
        String fieldsList = fields.stream()
                .map(Field::getName)
                .collect(Collectors.joining("=?, "));
        fieldsList = fieldsList.concat("=?");
        return "update "+ metaData.getName() +" set " + fieldsList + " where " + metaData.getIdField().getName() + " = ?";
    }
}
