package cpsc326;

class Interpreter implements Expr.Visitor<Object>{
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            OurPL.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // todo complete function
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        // todo complete function
    }

    private void checkNumberOperand(Token operator, Object operand) {
        // todo complete function
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        // todo complete function
    }

    private boolean isTruthy(Object object) {
        // todo complete function
    }

    private boolean isEqual(Object left, Object right) {
        // todo complete function
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        // todo complete function
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // todo complete function

        switch(expr.operator.type){
            // todo complete function
            
        }

        return null;
    }
}
