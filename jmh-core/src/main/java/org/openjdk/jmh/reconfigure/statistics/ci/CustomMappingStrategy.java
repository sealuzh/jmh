package org.openjdk.jmh.reconfigure.statistics.ci;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

    public CustomMappingStrategy(Class<T> type) {
        super();
        setType(type);
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        super.generateHeader(bean);
        int numColumns = findMaxFieldIndex();

        String[] ret = new String[numColumns + 1];
        for (int i = 0; i <= numColumns; i++) {
            ret[i] = findField(i).getField().getName();
        }

        return ret;
    }
}
