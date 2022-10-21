package com.config.dataSource;

import com.utils.AESUtil;
import com.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AESEncryptHandler extends BaseTypeHandler {


    @Value("${qcvisit.encrypt}")
    private boolean encrypt;

    /**
     * 设置非空参数
     * @param preparedStatement
     * @param i
     * @param o
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        if (encrypt){
            preparedStatement.setString(i,  AESUtil.encode((String) o, Constant.AES_KEY));
        }
    }

    /**
     * 获得可空结果
     * @param resultSet
     * @param s
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String columnValue = resultSet.getString(s);
        if (encrypt){
            if (StringUtils.isNotEmpty(columnValue)){
                return  AESUtil.decode(columnValue, Constant.AES_KEY);
            }else {
                return columnValue;
            }
        }else {
            return columnValue;
        }
    }

    /**
     * 获得可空结果
     * @param resultSet
     * @param i
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String columnValue = resultSet.getString(i);
        if (encrypt){
            if (StringUtils.isNotEmpty(columnValue)){
                return  AESUtil.decode(columnValue, Constant.AES_KEY);
            }else {
                return columnValue;
            }
        }else {
            return columnValue;
        }
    }

    /**
     * 获得可空结果
     * @param callableStatement
     * @param i
     * @return
     * @throws SQLException
     */
    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String columnValue = callableStatement.getString(i);
        if (encrypt){
            if (StringUtils.isNotEmpty(columnValue)){
                return  AESUtil.decode(columnValue, Constant.AES_KEY);
            }else {
                return columnValue;
            }
        }else {
            return columnValue;
        }
    }
}
