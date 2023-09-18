package faq.fastreport.ru.db

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

fun BaseTable<*>.intArray(name: String): Column<Array<Int?>> {
    return registerColumn(name, IntArraySqlType)
}

object IntArraySqlType : SqlType<Array<Int?>>(Types.ARRAY, "integer[]") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Array<Int?>) {
        ps.setObject(index, parameter)
    }

    override fun doGetResult(rs: ResultSet, index: Int): Array<Int?>? {
        val sqlArray = rs.getArray(index) ?: return null
        try {
            val objectArray = sqlArray.array as Array<Any?>?
            return objectArray?.mapNotNull { it as? Int }?.toTypedArray()
        } finally {
            sqlArray.free()
        }
    }
}