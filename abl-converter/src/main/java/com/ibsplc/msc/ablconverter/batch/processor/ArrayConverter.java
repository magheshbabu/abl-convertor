package com.ibsplc.msc.ablconverter.batch.processor;


import com.ibsplc.msc.ablconverter.array.data.TableFieldHolder;
import com.ibsplc.msc.ablconverter.core.TableAndField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArrayConverter extends BaseConverter {

    @Autowired
    TableFieldHolder fieldHolder;
    String strToFind = "";
    String strToReplace = "";
    int matchesFound = 0;
    private String noOfClosingBracketsRequired;

    @Override
    protected String convert(String lineToProcess) {

        String replacedLine = lineToProcess;

        replacedLine = processLine(replacedLine);

        return replacedLine;
    }

    private String processLine(String lineToProcess) {

        String replacedLine = lineToProcess;
        String strToFind = "";
        String strToReplace = "";
        ListIterator < TableAndField > itr = fieldHolder.getTableAndField().listIterator();

        while (itr.hasNext()) {
            TableAndField tf = (TableAndField) itr.next();
            if (replacedLine.toLowerCase().indexOf(tf.getTableNameDotFieldName().toLowerCase()) != -1) { //============================================================CHANGE HERE TO CATCH EXACT MATCH================
                replacedLine = splitAndDecide(lineToProcess, tf);
            }

        }


        return replacedLine;
    }


    private String splitAndDecide(String lineToProcess, TableAndField tf) {

        boolean HAS_ASSIGN_STATEMENT = false; // if the line has got a statement 'assign'
        boolean IS_FIRST_WORD = false; // if tablename.fieldname is the first word of the line eg:"prodinv.gar-avl[t]  = prodinv.gar-avl[t]  - p-cabs."
        String returnValue = "";

        String splittedParts[] = lineToProcess.split(" ");

        //Checking whether...If the line contains the keyword 'assign'
        for (String part: splittedParts) {
            if (part.trim().toLowerCase().equals("assign")) {
                HAS_ASSIGN_STATEMENT = true;
            }

        }


        //checking whether the line starts with a tablename.fieldname
        //eg:"prodinv.gar-avl[t]  = prodinv.gar-avl[t]  - p-cabs."
        if (fieldHolder.ExistsTableAndField(splittedParts[0].trim().replaceAll("(\\[)(.*?)(\\])", ""))) {
            IS_FIRST_WORD = true;
        }


        // if the line has the statement 'assign' then handle using 'assign' handler
        if (HAS_ASSIGN_STATEMENT) {
            returnValue = handleASSIGNSTATEMENTReplacement(lineToProcess, tf);
        } else if (IS_FIRST_WORD) // if the first word of the line starts with tablename.fieldname
        {
            returnValue = handleASSIGNSTATEMENTReplacement(lineToProcess, tf); // it can be handled using the same case as assign
        } else {
            returnValue = handleNORMALReplacement(lineToProcess, tf);
        }

        return returnValue;

    }


    private String handleNORMALReplacement(String lineToProcess, TableAndField tf) {
        String replacedLine = lineToProcess;

        strToFind = "(?i)(?<!-)(\\b" + tf.getTableName() + "\\." + tf.getFieldName() + "\\b)\\s*(\\[)(.*?)(\\])";


        if (tf.getDataType().toLowerCase().equals("character")) { // if data type is string...dont need to do 'string' typecast (according to massi's doc)
            strToReplace = "entry\\($3,$1,\";\"\\)\\)";
        } else if (tf.getDataType().toLowerCase().equals("logical")) {
            strToReplace = tf.getDataType() + "\\(integer(entry\\($3,$1,\";\"\\)\\)";
        } else //data type is not string...then do 'string' typecast (according to massi's doc)
        {
            strToReplace = tf.getDataType() + "\\(entry\\($3,$1,\";\"\\)\\)";

        }

        replacedLine = replacedLine.replaceAll(strToFind, strToReplace);

        return replacedLine;
    }


    /**
     * In case of lines with an ASSIGN statement in it..we dont need to add data type on the LHS. We need to add data type on RHS.
     * Also, on the RHS, we need to add a 'string' type conversion. An example is shown below
     * ORGINAL LINE IS  "then assign prodinv.sold[c] = prodinv.sold[c] + p-cabs"
     * REPLACED LINE IS "then assign entry(c,prodinv.sold) = string(integer(entry(c,prodinv.sold)) + p-cabs)"
     */
    private String handleASSIGNSTATEMENTReplacement(String lineToProcess, TableAndField tf) {
        String replacedLine = lineToProcess;
        String strLHS = "";
        String strRHS = "";
        int loopCounter = 0;
  /*
   * In this case..the logic steps that we apply is defined below
   * 1. Split the line using "="
   * 2. Then...we have two parts of equal sign (LHS, RHS)
   * 3. On LHS, we will not replace the datatype part
   * 4. On RHS, we will replace datatype and also add an extra typecasting of 'string'
   */

        String splittedParts[] = lineToProcess.split("=");


        strLHS = splittedParts[0];
        strLHS = replaceLHS(strLHS, tf);

        for (int i = 1; i < splittedParts.length; i++) {

            String before = splittedParts[i];
            splittedParts[i] = replaceRHS(splittedParts[i], tf);
            String after = splittedParts[i];

            if (!before.equals(after)) { // if changes has been made to RHS string...then need to add bracket
                splittedParts[i] = SuffixClosingBracketsAndDot(splittedParts[i]);
            }

            strRHS += splittedParts[i];
        }
        //joining both lines back to a single line
        replacedLine = strLHS + " = " + strRHS;

        //}
        return replacedLine;
    }

    /**
     * In case of lines where tablename.fieldname is at the begining of the line. an example is shown below
     * //eg:"prodinv.gar-avl[t]  = prodinv.gar-avl[t]  - p-cabs."
     * <p>
     * In this case..we dont need to add data type on the LHS. We need to add data type on RHS.
     * Also, on the RHS, we need to add a 'string' type conversion. An example is shown below
     * ORGINAL LINE IS  "prodinv.gar-avl[t]  = prodinv.gar-avl[t]  - p-cabs."
     * REPLACED LINE IS "entry(t,prodinv.gar-avl)   =  string(integer(entry(t,prodinv.gar-avl))  - p-cabs)."
     */
    private String handleISINFIRSTOFLINESTATEMENTReplacement(String lineToProcess, TableAndField tf) {
        String replacedLine = lineToProcess;
        String strLHS = "";
        String strRHS = "";
        int loopCounter = 0;
  /*
   * In this case..the logic steps that we apply is defined below
   * 1. Split the line using "="
   * 2. Then...we have two parts of equal sign (LHS, RHS)
   * 3. On LHS, we will not replace the datatype part
   * 4. On RHS, we will replace datatype and also add an extra typecasting of 'string'
   */

        String splittedParts[] = lineToProcess.split("=");

        strLHS = splittedParts[0];
        strLHS = replaceLHS(strLHS, tf);

        for (int i = 1; i < splittedParts.length; i++) {

            String before = splittedParts[i];
            splittedParts[i] = replaceRHS(splittedParts[i], tf);
            String after = splittedParts[i];

            if (!before.equals(after)) { // if changes has been made to RHS string...then need to add bracket
                splittedParts[i] = SuffixClosingBracketsAndDot(splittedParts[i]);
            }

            strRHS += splittedParts[i];
        }
        //joining both lines back to a single line
        replacedLine = strLHS + " = " + strRHS;
        //}
        return replacedLine;
    }


    /*
     * Handles replacement for the RIGHT HAND SIDE of the equal sign
     */
    private String replaceLHS(String linetoProcess, TableAndField tf) {

        //(?<!-)(\bproddefn.apply-to\b)\s*(\[)(.*?)(\])
        strToFind = "(?i)(?<!-)(\\b" + tf.getTableName() + "\\." + tf.getFieldName() + "\\b)\\s*(\\[)(.*?)(\\])";
        strToReplace = "entry\\($3,$1,\";\"\\)";
        return linetoProcess.replaceAll(strToFind, strToReplace);

    }

    /*
     * Handles replacement for the RIGHT HAND SIDE of the equal sign
     */
    private String replaceRHS(String linetoProcess, TableAndField tf) {

        //strToFind = "(?<!-)(\\b" + tf.getTableName()  + "\\." + tf.getFieldName() + ")\\[(.*?)(\\])";
        strToFind = "(?i)(?<!-)(\\b" + tf.getTableName() + "\\." + tf.getFieldName() + "\\b)\\s*(\\[)(.*?)(\\])";

        if (tf.getDataType().toLowerCase().equals("character")) { // if data type is string...dont need to do 'string' typecast (according to massi's doc)
            strToReplace = "entry\\($3,$1,\";\"\\)\\)";
        } else if (tf.getDataType().toLowerCase().equals("logical")) {
            strToReplace = tf.getDataType() + "\\(integer(entry\\($3,$1,\";\"\\)\\)";
        } else //data type is not string...then do 'string' typecast (according to massi's doc)
        {
            strToReplace = "String(" + tf.getDataType() + "\\(entry\\($3,$1,\";\"\\)\\)";


        }


        Pattern p = Pattern.compile(strToFind);
        Matcher m = p.matcher(linetoProcess);

        matchesFound = 0; // there can be more than one match in a string...so resetting it before counting the occurance
        noOfClosingBracketsRequired = "";
        while (m.find()) // we need to keep track of count because...we need to add..that many closing brackets..on 'SuffixClosingBracketsAndDot' method
        {
            matchesFound = matchesFound + 1; //
            noOfClosingBracketsRequired = noOfClosingBracketsRequired + ")"; // preparing the closing brackets required
        }


        if (matchesFound >= 1) { // if we found at least one or more matches...then ..
            return linetoProcess.replaceAll(strToFind, strToReplace);
        } else {
            if (!tf.getDataType().toLowerCase().equals("character")) {
                return "STRING(" + linetoProcess.trim();
            }

            return linetoProcess;
        }


    }


    /*
     * Add a bracket at the end
     * adding the dot back...after the bracket
     */
    private String SuffixClosingBracketsAndDot(String lineToProcess) {
        if (noOfClosingBracketsRequired.trim().length() == 0) { // if there is zero matches of pattern...then we need atleast one closing bracket..so.. adding one
            noOfClosingBracketsRequired = ")";
        }
        String strRHS = lineToProcess;
        strRHS = strRHS.trim(); // trimming to add the bracket..after the DOT
        if (strRHS != null && strRHS.length() > 0 && strRHS.charAt(strRHS.length() - 1) == '.') {
            strRHS = strRHS.substring(0, strRHS.length() - 1); // removing the dot first..and then...
            strRHS = strRHS + noOfClosingBracketsRequired + "."; // adding the dot back...after the bracket
        } else if (strRHS != null && strRHS.length() > 0 && strRHS.charAt(strRHS.length() - 1) != '.') { // if last character is not a dot
            strRHS = strRHS + noOfClosingBracketsRequired; //then simply add a bracket
        }

        return strRHS;
    }

}