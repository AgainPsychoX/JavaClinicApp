package pl.edu.ur.pz.clinicapp.utils;

import org.hibernate.tool.hbm2ddl.ImportScriptException;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads SQL files as statements that allows for multi-line statements and strings (single or dollar quoting).
 *
 * Notes:
 *  + Single quotes can be escaped using two single quote characters (i.e. 'Vel''Koz').
 *  + Dollar quoting can have tags (i.e. $quote$Let's see what they're made of.$quote$).
 *  + Dollar quoting can have comments inside.
 */
public class CustomImportSqlCommandExtractor implements ImportSqlCommandExtractor {
    private static final Logger logger = Logger.getLogger(CustomImportSqlCommandExtractor.class.getName());

    @Override
    public String[] extractCommands(Reader reader) {
        final var bufferedReader = new BufferedReader(reader);
        final var statementList = new LinkedList<String>();
        String expecting = null;
        StringBuilder multilineValue = new StringBuilder();

        Pattern anythingInterestingPattern = Pattern.compile("'|\\$\\w*\\$|--|/\\*|//|;");

        try {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                line += '\n';
                while (line.length() > 0) {
                    logger.finer("Line content (" + line.length() + "): " + line);

                    // If expecting anything to end, look for it
                    if (expecting != null) {
                        logger.finer("Expecting: " + expecting);

                        final var index = line.indexOf(expecting);
                        if (index == -1) {
                            multilineValue.append(line);
                            break; // go directly to next line
                        }
                        if (expecting.startsWith("$")) {
                            multilineValue.append(line, 0, index + expecting.length());
                            line = line.substring(index + expecting.length());
                            expecting = null;
                        }
                        else if ("*/".equals(expecting)) {
                            line = line.substring(index + 2);
                            expecting = null;
                        }
                        else if ("'".equals(expecting)) {
                            if (index + 1 < line.length() && line.charAt(index + 1) == '\'') {
                                multilineValue.append(line, 0, index + 1);
                                // still expecting, as current one was escaped, so itself part of quoted content
                            }
                            else {
                                multilineValue.append(line, 0, index + 1);
                                expecting = null;
                            }
                            line = line.substring(index + 1);
                        }
                        continue; // ... with rest of the line
                    }

                    // Then, if not expecting anything, see if anything comes up
                    Matcher matcher = anythingInterestingPattern.matcher(line);
                    if (matcher.find()) {
                        expecting = matcher.group();

                        switch (expecting) {
                            case "--", "//" -> {
                                multilineValue.append(line, 0, matcher.start());
                                line = ""; // breaks out ignoring rest of the line
                                expecting = null; // ignoring rest of the line
                            }
                            case "/*" -> {
                                multilineValue.append(line, 0, matcher.start());
                                expecting = "*/"; // then ignore rest until comment close
                            }
                            case ";" -> {
                                multilineValue.append(line, 0, matcher.start());
                                line = line.substring(matcher.start() + 1);
                                expecting = null;

                                // Add finished statement
                                final var statement = multilineValue.toString().trim();
                                statementList.add(statement);
                                multilineValue.delete(0, multilineValue.length());

                                // Debugging
                                logger.fine("Statement: " + statement);
                                if (logger.isLoggable(Level.FINEST)) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            default -> {
                                // Either single quote or dollar quoting
                                multilineValue.append(line, 0, matcher.start() + expecting.length());
                                line = line.substring(matcher.start() + expecting.length());
                            }
                        }
                    }
                    else {
                        // Nothing interesting, so add everything as multiline as it might be multiline statement
                        multilineValue.append(line);
                        break; // ... because whole line is added
                    }
                }
            }

            return statementList.toArray(new String[0]);
        }
        catch (IOException e) {
            throw new ImportScriptException("Error during import script parsing.", e);
        }
    }

//    private record Quoting(String start, String end, String escape) {}
//
//    final Quoting[] quotings = {
//            Quoting("/*", "*/", "\\");
//    }
//
//    private record QuotingInstance(int index, QuotingType type) {}
//
//    private Quoting findQuoteStart(String line) {
//        int index;
//        index = line.indexOf("/*");
//        if (index != -1) return index; // no, first indexOf's then search earilest
//    }

    private boolean isComment(final String line) {
        return line.startsWith( "--" ) || line.startsWith( "//" ) || line.startsWith( "/*" );
    }
}