package pl.edu.ur.pz.clinicapp.utils;

import org.hibernate.boot.model.naming.*;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class HibernateCustomPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    @Override
    public Identifier toPhysicalColumnName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return snakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return snakeCase(identifier);
    }

    protected Identifier snakeCase(final Identifier identifier) {
        return new Identifier(snakeCase(identifier.getText()), identifier.isQuoted());
    }

    protected static String snakeCase(String name) {
        final StringBuilder buf = new StringBuilder(name);
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1)) &&
                    Character.isUpperCase(buf.charAt(i)) &&
                    Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
    }
}
