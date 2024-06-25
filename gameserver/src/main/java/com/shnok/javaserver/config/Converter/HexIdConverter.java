package com.shnok.javaserver.config.Converter;

import java.lang.reflect.Method;
import java.math.BigInteger;

import org.aeonbits.owner.Converter;
import org.apache.logging.log4j.util.Strings;

/**
 * Hex Id Converter.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class HexIdConverter implements Converter<BigInteger> {

    @Override
    public BigInteger convert(Method method, String input) {
        if (Strings.isBlank(input)) {
            return null;
        }
        return new BigInteger(input, 16);
    }
}
