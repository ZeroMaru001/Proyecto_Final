import java.beans.Expression;
import java.util.List;
public class ASDR implements Parser {
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {
        PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Consulta correcta");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
        }
        return false;
    }

    // PROGRAM -> DECLARATION

    private void PROGRAM(){
        DECLARATION();
    }

    /** DECLARACIONES **/

    // DECLARATION -> FUN_DECL DECLARATION
    //             -> VAR_DECL DECLARATION
    //             -> STATEMENT DECLARATION
    //             -> Ɛ
    private void DECLARATION(){
        if(hayErrores)
            return;

        if(TipoToken.FUN == preanalisis.tipo){
            // -> FUN_DECL DECLARATION
            FUN_DECL();
            DECLARATION();
        } else if (TipoToken.VAR == preanalisis.tipo) {
            // -> VAR_DECL DECLARATION
            VAR_DECL();
            DECLARATION();
        } else if(TipoToken.BANG == preanalisis.tipo ||
                  TipoToken.MINUS == preanalisis.tipo ||
                  TipoToken.TRUE == preanalisis.tipo ||
                  TipoToken.FALSE == preanalisis.tipo ||
                  TipoToken.NULL == preanalisis.tipo ||
                  TipoToken.NUMBER == preanalisis.tipo ||
                  TipoToken.STRING == preanalisis.tipo ||
                  TipoToken.IDENTIFIER == preanalisis.tipo ||
                  TipoToken.LEFT_PAREN == preanalisis.tipo ||
                  TipoToken.FOR == preanalisis.tipo ||
                  TipoToken.IF == preanalisis.tipo ||
                  TipoToken.PRINT == preanalisis.tipo ||
                  TipoToken.RETURN == preanalisis.tipo ||
                  TipoToken.WHILE == preanalisis.tipo ||
                  TipoToken.LEFT_BRACE == preanalisis.tipo ) {
            // -> STATEMENT DECLARATION
            STATEMENT();
            DECLARATION();
        }
    }

    //FUN_DECL -> fun FUNCTION
    private void FUN_DECL(){
        if(hayErrores)
            return;

        if(TipoToken.FUN == preanalisis.tipo){
            match(TipoToken.FUN);
            FUNCTION();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un fun");
        }
    }

    //VAR_DECL -> var id VAR_INIT ;
    private void VAR_DECL(){
        if(hayErrores)
            return;

        if (TipoToken.VAR == preanalisis.tipo){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            VAR_INIT();
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un var");
        }
    }

    //VAR_INIT -> = EXPRESSION | Ɛ
    private void VAR_INIT(){
        if(hayErrores)
            return;

        if (TipoToken.EQUAL == preanalisis.tipo){
            match(TipoToken.EQUAL);
            expression();
        }

    }

    /** SENTENCIAS **/

    //STATEMENT -> EXPR_STMT
    //-> FOR_STMT
    //-> IF_STMT
    //-> PRINT_STMT
    //-> RETURN_STMT
    //-> WHILE_STMT
    //-> BLOCK
    private void STATEMENT(){
        if(hayErrores)
            return;

        if(TipoToken.FOR == preanalisis.tipo){
            //-> FOR_STMT
            FOR_STMT();
        } else if (TipoToken.IF == preanalisis.tipo) {
            //-> IF_STMT
            IF_STMT();
        } else if (TipoToken.PRINT == preanalisis.tipo) {
            //-> PRINT_STMT
            PRINT_STMT();
        } else if (TipoToken.RETURN == preanalisis.tipo) {
            //-> RETURN_STMT
            RETURN_STMT();
        } else if (TipoToken.WHILE == preanalisis.tipo) {
            //-> WHILE_STMT
            WHILE_STMT();
        } else if (TipoToken.LEFT_BRACE == preanalisis.tipo) {
            //-> BLOCK
            BLOCK();
        } else {
            //-> EXPR_STMT
            EXPR_STMT();
        }
    }

    //EXPR_STMT -> EXPRESSION ;
    private void EXPR_STMT(){
        if(hayErrores)
            return;
            expression();
        match(TipoToken.SEMICOLON);
    }

    //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private void FOR_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.FOR == preanalisis.tipo){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un for");
        }
    }

    //FOR_STMT_1 -> VAR_DECL
    //           -> EXPR_STMT
    //           -> ;
    private void FOR_STMT_1(){
        if(hayErrores)
            return;

        if(TipoToken.VAR == preanalisis.tipo){
            //-> VAR_DECL
            VAR_DECL();
        } else if (TipoToken.SEMICOLON == preanalisis.tipo) {
            //-> ;
            match(TipoToken.SEMICOLON);
        } else {
            //-> EXPR_STMT
            EXPR_STMT();
        }
    }

    //FOR_STMT_2 -> EXPRESSION;
    //           -> ;
    private void FOR_STMT_2(){
        if(hayErrores)
            return;

        if(TipoToken.SEMICOLON == preanalisis.tipo){
            //-> ;
            match(TipoToken.SEMICOLON);
        } else {
            //->EXPRESSION;
            expression();
            match(TipoToken.SEMICOLON);
        }
    }

    //FOR_STMT_3 -> EXPRESSION
    //           -> Ɛ
    private void FOR_STMT_3(){
        if(hayErrores)
            return;

        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            expression();
        }

    }

    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private void IF_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.IF == preanalisis.tipo){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            expression();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
            ELSE_STATEMENT();
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un if");
        }
    }

    // ELSE_STATEMENT -> else STATEMENT
    //                -> Ɛ
    private void ELSE_STATEMENT(){
        if(hayErrores)
            return;

        if (TipoToken.ELSE == preanalisis.tipo){
            match(TipoToken.ELSE);
            STATEMENT();
        }
    }

    //PRINT_STMT -> print EXPRESSION ;
    private void PRINT_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.PRINT == preanalisis.tipo){
            match(TipoToken.PRINT);
            expression();
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un print");
        }
    }

    //RETURN_STMT -> return RETURN_EXP_OPC ;
    private void RETURN_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.RETURN == preanalisis.tipo){
            match(TipoToken.RETURN);
            RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un return");
        }
    }

    //RETURN_EXP_OPC -> EXPRESSION
    //               -> Ɛ
    private void RETURN_EXP_OPC(){
        if(hayErrores)
            return;
        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            expression();
        }
    }

    //WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    private void WHILE_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.WHILE == preanalisis.tipo){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            expression();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un while");
        }
    }

    //BLOCK -> { DECLARATION }
    private void BLOCK() {
        if (hayErrores)
            return;

        if (TipoToken.LEFT_BRACE == preanalisis.tipo) {
            match(TipoToken.LEFT_BRACE);
            DECLARATION();
            match(TipoToken.RIGHT_BRACE);
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un {");
        }
    }

    /**EXPRESIONES*/

        public void expression(){
            assignment();
        }
        public void assignment(){
            logic_or();
            if(this.preanalisis.getTipo() == TipoToken.EQUAL){
                assignment_opc();
            }
        }
        public void assignment_opc(){
            if(this.preanalisis.getTipo() == TipoToken.EQUAL){
                match(TipoToken.EQUAL);
                expression();
            }
        }
        public void logic_or(){
            logic_and();
            if(this.preanalisis.getTipo() == TipoToken.OR){
                logic_or_2();
            }
        }
        public void logic_or_2(){
            if(this.preanalisis.getTipo() == TipoToken.OR){
                match(TipoToken.OR);
                logic_and();
                logic_or_2();
            }
        }
        public void logic_and(){
            equality();
            if(this.preanalisis.getTipo() == TipoToken.AND){
                logic_and_2();
            }
        }
        public void logic_and_2() {
            switch (this.preanalisis.getTipo()) {
                case BANG_EQUAL:
                    match(TipoToken.BANG_EQUAL);
                    comparison();
                    logic_and_2();
                    break;

                case EQUAL_EQUAL:
                    match(TipoToken.EQUAL_EQUAL);
                    comparison();
                    logic_and_2();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void equality(){
            comparison();
            if (this.preanalisis.getTipo() == TipoToken.BANG_EQUAL || this.preanalisis.getTipo() == TipoToken.EQUAL_EQUAL){
                equality_2();
            }
        }
        public void equality_2(){
            switch (this.preanalisis.getTipo()){
                case BANG_EQUAL:
                    match(TipoToken.BANG_EQUAL);
                    comparison();
                    equality_2();
                    break;

                case EQUAL_EQUAL:
                    match(TipoToken.EQUAL_EQUAL);
                    comparison();
                    equality_2();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void comparison(){
            term();
            if (this.preanalisis.getTipo() == TipoToken.GREATER || this.preanalisis.getTipo() == TipoToken.GREATER_EQUAL || this.preanalisis.getTipo() == TipoToken.LESS || this.preanalisis.getTipo() == TipoToken.LESS_EQUAL){
                comparison_2();
            }
         }
        public void comparison_2(){
            switch (this.preanalisis.getTipo()){
                case GREATER:
                    match(TipoToken.GREATER);
                    term();
                    comparison_2();
                    break;

                case GREATER_EQUAL:
                    match(TipoToken.GREATER_EQUAL);
                    term();
                    comparison_2();
                    break;

                case LESS:
                    match(TipoToken.LESS);
                    term();
                    comparison_2();
                    break;

                case LESS_EQUAL:
                    match(TipoToken.LESS_EQUAL);
                    term();
                    comparison_2();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void term(){
            factor();
            if (this.preanalisis.getTipo() == TipoToken.MINUS || this.preanalisis.getTipo() == TipoToken.PLUS)
                term_2();
            }

        public void term_2(){
            switch (this.preanalisis.getTipo()){
                case MINUS:
                    match(TipoToken.MINUS);
                    factor();
                    term_2();
                    break;

                case PLUS:
                    match(TipoToken.PLUS);
                    factor();
                    term_2();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void factor() {
            unary();
            if (this.preanalisis.getTipo() == TipoToken.STAR || this.preanalisis.getTipo() == TipoToken.SLASH) {
                factor_2();
            }
        }
        public void factor_2(){
            switch(this.preanalisis.getTipo()){
                case SLASH:
                    match(TipoToken.SLASH);
                    unary();
                    factor_2();
                    break;

                case STAR:
                    match(TipoToken.STAR);
                    unary();
                    factor_2();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void unary(){
            switch(this.preanalisis.getTipo()){
                case BANG:
                    match(TipoToken.BANG);
                    unary();
                    break;

                case MINUS:
                    match(TipoToken.MINUS);
                    unary();
                    break;

                case TRUE,FALSE,NULL,NUMBER,STRING,IDENTIFIER,LEFT_PAREN:
                    call();
                    break;

                default:
                    this.hayErrores = true;
            }
        }
        public void call(){
            primary();
            if (preanalisis.tipo == TipoToken.LEFT_PAREN)
                call_2();
        }
        public void call_2(){
            if (preanalisis.tipo == TipoToken.LEFT_PAREN){
                match(TipoToken.LEFT_PAREN);
                ARGUMENTS_OPC();
                match(TipoToken.RIGHT_PAREN);
                call_2();
            }
        }
        public void primary() {
            switch (this.preanalisis.getTipo()) {
                case TRUE:
                    match(TipoToken.TRUE);
                    break;

                case FALSE:
                    match(TipoToken.FALSE);
                    break;

                case NULL:
                    match(TipoToken.NULL);
                    break;

                case NUMBER:
                    match(TipoToken.NUMBER);
                    Token number = anterior();
                    break;

                case STRING:
                    match(TipoToken.STRING);
                    Token string = anterior();
                    break;

                case IDENTIFIER:
                    match(TipoToken.IDENTIFIER);
                    Token identifier = anterior();
                    break;

                case LEFT_PAREN:
                    match(TipoToken.LEFT_PAREN);
                    expression();
                    match(TipoToken.RIGHT_PAREN);
                    expression();
                    break;

                default:
                    this.hayErrores = true;

            }
        }

    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    private void FUNCTION() {
        if (hayErrores) return;

        // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
        if (TipoToken.IDENTIFIER == preanalisis.tipo) {
            match(TipoToken.IDENTIFIER);
            match(TipoToken.LEFT_PAREN);
            PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            BLOCK();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba id");
        }
    }

    // FUNCTIONS -> FUN_DECL FUNCTIONS | Ɛ
    private void FUNCTIONS() {
        if (hayErrores)
            return;

        if (TipoToken.FUN == preanalisis.tipo) {
            FUN_DECL();
            FUNCTIONS();
        }
    }

    // PARAMETERS_OPC -> PARAMETERS | Ɛ
    private void PARAMETERS_OPC() {
        if (hayErrores)
            return;

        if (TipoToken.IDENTIFIER  == preanalisis.tipo) {
            PARAMETERS();
        }
    }

    // PARAMETERS -> id PARAMETERS_2
    private void PARAMETERS() {
        if (hayErrores) return;

        // PARAMETERS -> id PARAMETERS_2
        if (TipoToken.IDENTIFIER  == preanalisis.tipo) {
            match(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba id");
        }
    }

    // PARAMETERS_2 -> , id PARAMETERS_2 | Ɛ
    private void PARAMETERS_2() {
        if (hayErrores)
            return;

        while (TipoToken.COMMA == preanalisis.tipo) {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        }
    }

    // ARGUMENTS_OPC -> EXPRESSION ARGUMENTS | Ɛ
    private void ARGUMENTS_OPC() {
        if (hayErrores)
            return;

        if (TipoToken.BANG == preanalisis.tipo || TipoToken.MINUS == preanalisis.tipo ||
            TipoToken.TRUE == preanalisis.tipo || TipoToken.FALSE == preanalisis.tipo ||
            TipoToken.NULL == preanalisis.tipo || TipoToken.NUMBER == preanalisis.tipo ||
            TipoToken.STRING == preanalisis.tipo || TipoToken.IDENTIFIER == preanalisis.tipo ||
            TipoToken.LEFT_PAREN == preanalisis.tipo) {
            expression();
            ARGUMENTS();
        }
    }

    // ARGUMENTS -> , EXPRESSION ARGUMENTS | Ɛ
    private void ARGUMENTS() {
        if (hayErrores)
            return;

        while (TipoToken.COMMA  == preanalisis.tipo) {
            match(TipoToken.COMMA);
            if (TipoToken.BANG == preanalisis.tipo || TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo || TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo || TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo || TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo) {
                expression();
                ARGUMENTS();
            }
        }
    }

    //Comparar tipo de token
    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado:");
            System.out.println("Se puso:" + preanalisis.tipo);
            System.out.println("Se esperaba:" + tt);
        }

    }
    private Token anterior(){
        return this.tokens.get(i-1);
    }
}
