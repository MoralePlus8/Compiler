package frontend;

import global.Enums;
import global.ErrorPair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static global.StaticConst.keywordsTable;
import static global.StaticVariable.*;

public class Lexer {

    public static void getCode(String filePath){
        try{
            byte[] content = Files.readAllBytes(Paths.get(filePath));
            code = new String(content);
            code += "\0";
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void getNextCharacter(){
        character=code.charAt(characterCounter);
        characterCounter++;
    }

    public static void clearToken(){
        token = "";
    }

    public static void retract(){
        characterCounter--;
    }

    public static Enums.SymbolCode getSymbolCodeOfIdentifier(){
        if(keywordsTable.containsKey(token)){
            return keywordsTable.get(token);
        }
        return Enums.SymbolCode.IDENFR;
    }

    public static boolean isSpace(){
        return character == ' ';
    }

    public static boolean isNewLine(){
        return character == 10;
    }

    public static boolean isTab(){
        return character == 9;
    }

    public static boolean isLetter(){
        return (character >= 'a' && character <= 'z' || character >='A' && character <= 'Z');
    }

    public static boolean isNumber(){
        return (character >= '0' && character <= '9');
    }

    public static boolean isSingleQuotationMark(){
        return character == '\'';
    }

    public static boolean isDoubleQuotationMark(){
        return character == '"';
    }

    public static boolean isBackSlash(){
        return character == '\\';
    }

    public static void clearFile(String filePath){
        try{
            File f = new File(filePath);
            FileWriter fw = new FileWriter(f);
            fw.write("");
            fw.flush();
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void analyseNextToken(){
        clearToken();
        isNote = false;
        getNextCharacter();
        if(character == '\0') return;
        while(isSpace() || isNewLine() || isTab()) {
            if(isNewLine()){
                lineCounter++;
            }
            getNextCharacter();
        }

        //如果是字母开头
        if(isLetter() || character == '_'){
            while(isLetter() || isNumber() || character == '_'){
                token += character;
                getNextCharacter();
            }
            retract();
            symbol = getSymbolCodeOfIdentifier();
        }

        //如果是数字开头
        else if(isNumber()){
            while(isNumber()){
                token += character;
                getNextCharacter();
            }
            retract();
            symbol = Enums.SymbolCode.INTCON;
        }

        else if(isSingleQuotationMark()){
            token += character;
            getNextCharacter();
            token += character;
            if(isBackSlash()){
                getNextCharacter();
                token += character;
            }
            getNextCharacter();
            token += character;
            symbol = Enums.SymbolCode.CHRCON;
        }

        else if(isDoubleQuotationMark()){
            token += character;
            getNextCharacter();
            while(!isDoubleQuotationMark()){
                token += character;
                getNextCharacter();
            }
            token += character;
            symbol = Enums.SymbolCode.STRCON;
        }

        else if(character == '&'){
            token += character;
            getNextCharacter();
            if(character == '&'){
                token += '&';
                symbol = Enums.SymbolCode.AND;
            }
            else{
                errorCode = Enums.ErrorCode.a;
                errorLine = lineCounter;
                lexerErrors.add(new ErrorPair(errorLine, errorCode));
                retract();
            }
        }

        else if(character == '|'){
            token += character;
            getNextCharacter();
            if(character == '|'){
                token += '|';
                symbol = Enums.SymbolCode.OR;
            }
            else{
                errorCode = Enums.ErrorCode.a;
                errorLine = lineCounter;
                lexerErrors.add(new ErrorPair(errorLine, errorCode));
                retract();
            }
        }

        else if(character == '!'){
            token += character;
            getNextCharacter();
            if(character == '='){
                token += character;
                symbol = Enums.SymbolCode.NEQ;
            }
            else{
                symbol = Enums.SymbolCode.NOT;
                retract();
            }
        }

        else if(character == '+'){
            token += character;
            symbol = Enums.SymbolCode.PLUS;
        }

        else if(character == '-'){
            token += character;
            symbol = Enums.SymbolCode.MINU;
        }

        else if(character == '*'){
            token += character;
            symbol = Enums.SymbolCode.MULT;
        }

        else if(character == '/'){
            token += character;
            getNextCharacter();
            if(character == '*'){
                isNote = true;
                do{
                    do{
                        getNextCharacter();
                    }while(character != '*');

                    do{
                        getNextCharacter();
                        if(character == '/'){
                            clearToken();
                            return;
                        }
                    }while(character == '*');
                }while(true);
            }

            else if(character == '/'){
                isNote = true;
                do{
                    getNextCharacter();
                }while(!isNewLine() && character != '\0');
                clearToken();
                return;
            }

            symbol = Enums.SymbolCode.DIV;
            retract();
        }

        else if(character == '%'){
            token += character;
            symbol = Enums.SymbolCode.MOD;
        }

        else if(character == '<'){
            token += character;
            getNextCharacter();
            if(character == '='){
                token += character;
                symbol = Enums.SymbolCode.LEQ;
            }
            else{
                symbol = Enums.SymbolCode.LSS;
                retract();
            }
        }

        else if(character == '>'){
            token += character;
            getNextCharacter();
            if(character == '='){
                token += character;
                symbol = Enums.SymbolCode.GEQ;
            }
            else{
                symbol = Enums.SymbolCode.GRE;
                retract();
            }
        }

        else if(character == '='){
            token += character;
            getNextCharacter();
            if(character == '='){
                token += character;
                symbol = Enums.SymbolCode.EQL;
            }
            else{
                symbol = Enums.SymbolCode.ASSIGN;
                retract();
            }
        }

        else if(character == ';'){
            token += character;
            symbol = Enums.SymbolCode.SEMICN;
        }

        else if(character == ','){
            token += character;
            symbol = Enums.SymbolCode.COMMA;
        }

        else if(character == '('){
            token += character;
            symbol = Enums.SymbolCode.LPARENT;
        }

        else if(character == ')'){
            token += character;
            symbol = Enums.SymbolCode.RPARENT;
        }

        else if(character == '['){
            token += character;
            symbol = Enums.SymbolCode.LBRACK;
        }

        else if(character == ']'){
            token += character;
            symbol = Enums.SymbolCode.RBRACK;
        }

        else if(character == '{'){
            token += character;
            symbol = Enums.SymbolCode.LBRACE;
        }

        else if(character == '}'){
            token += character;
            symbol = Enums.SymbolCode.RBRACE;
        }
    }

    public static void lexer(){
        try{
            getCode("./testfile.txt");
            File output = new File("./lexer.txt");
            File error = new File("./error.txt");
            if (!output.exists()) output.createNewFile();
            if (!error.exists()) error.createNewFile();
            FileWriter writer = new FileWriter(output, true);
            FileWriter errorWriter = new FileWriter(error, true);
            clearFile("./lexer.txt");
            clearFile("./error.txt");
            lineCounter = 1;
            do {
                analyseNextToken();
                if(!isNote && character != '\0'){
                    writer.write(symbol.name()+' '+token+'\n');
                    writer.flush();
                }
            } while (character != '\0');

            if(!lexerErrors.isEmpty()){
                clearFile("./lexer.txt");
                for(ErrorPair errorPair : lexerErrors){
                    errorWriter.write(errorPair.toString()+'\n');
                    writer.flush();
                }
            }

            writer.close();
            errorWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
