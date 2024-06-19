package com.omprakash.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Stack;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView Equation,Result;
    MaterialButton button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,Decimal;
    MaterialButton Clear,AllClear,BackSpace,Calculate;
    MaterialButton Mod,Divide,Multiply,Add,Subtract;

    static boolean divide_zero = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        Equation=findViewById(R.id.equation);
        Result=findViewById(R.id.result);

        id_assign(button0,R.id.zero);
        id_assign(button1,R.id.one);
        id_assign(button2,R.id.two);
        id_assign(button3,R.id.three);
        id_assign(button4,R.id.four);
        id_assign(button5,R.id.five);
        id_assign(button6,R.id.six);
        id_assign(button7,R.id.seven);
        id_assign(button8,R.id.eight);
        id_assign(button9,R.id.nine);
        id_assign(Decimal,R.id.decimal);
        id_assign(Clear,R.id.clear);
        id_assign(AllClear,R.id.all_clear);
        id_assign(BackSpace,R.id.back_space);
        id_assign(Calculate,R.id.equals);
        id_assign(Mod,R.id.mod);
        id_assign(Divide,R.id.divide);
        id_assign(Multiply,R.id.multiply);
        id_assign(Add,R.id.add);
        id_assign(Subtract,R.id.subtract);
    }

    void id_assign(MaterialButton btn, int id)
    {
        btn=findViewById(id);
        btn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        MaterialButton button=(MaterialButton) v;
        String buttonText= button.getText().toString();
        String expression= Equation.getText().toString();

        switch (buttonText) {
            case "A":
//                Equation.setText("");
                expression = "";
                Result.setText("0");
                break;
            case "=":
                Equation.setText(Result.getText().toString());
                return;
            case "B":
                if (Equation.getText().toString().isEmpty())
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                else
                    expression = expression.substring(0, expression.length() - 1);
                break;
            case "C":
                expression = "";
                break;
            default:
                expression = expression + buttonText;
                break;
        }

        Equation.setText(expression);
        String res=getresult(expression);

        if(!res.equals("Err"))
        {
            Result.setText(res);
        }else if(divide_zero)
        {
            Toast.makeText(this, "Undefined", Toast.LENGTH_SHORT).show();
            Result.setText("");
            divide_zero=false;
        }else
        {
            Result.setText("");
        }

    }

    public static String getresult(String exp) {
        try {
            // Expression to be evaluated
            String expression = exp;

            // Convert infix expression to postfix
            StringBuilder postfix = new StringBuilder();
            Stack<Character> operatorStack = new Stack<>();
            boolean lastCharWasOperator = true;

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isDigit(c) || c == '.') {
                    // Append the entire number (including decimal part) to postfix
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        postfix.append(expression.charAt(i++));
                    }
                    postfix.append(' ');
                    i--; // Step back to process the last non-digit character in the next iteration
                    lastCharWasOperator = false;
                } else if (c == '(') {
                    operatorStack.push(c);
                    lastCharWasOperator = true;
                } else if (c == ')') {
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        postfix.append(operatorStack.pop()).append(' ');
                    }
                    operatorStack.pop(); // Discard '('
                    lastCharWasOperator = false;
                } else if (isOperator(c)) {
                    // Handle negative numbers
                    if (c == '-' && (i == 0 || lastCharWasOperator)) {
                        postfix.append(c); // Append negative sign to the number
                        continue;
                    }

                    while (!operatorStack.isEmpty() && precedence(c) <= precedence(operatorStack.peek())) {
                        postfix.append(operatorStack.pop()).append(' ');
                    }
                    operatorStack.push(c);
                    lastCharWasOperator = true;
                }
            }

            while (!operatorStack.isEmpty()) {
                postfix.append(operatorStack.pop()).append(' ');
            }

            // Evaluate the postfix expression
            Stack<Double> operandStack = new Stack<>();
            String[] tokens = postfix.toString().trim().split("\\s+");

            for (String token : tokens) {
                if (isNumeric(token)) {
                    operandStack.push(Double.parseDouble(token));
                } else {
                    double operand2 = operandStack.pop();
                    double operand1 = operandStack.pop();
                    double result = 0;
                    switch (token.charAt(0)) {
                        case '+':
                            result = operand1 + operand2;
                            break;
                        case '-':
                            result = operand1 - operand2;
                            break;
                        case '*':
                            result = operand1 * operand2;
                            break;
                        case '/':
                            if (operand2 == 0) {
                                divide_zero = true;
                                throw new ArithmeticException("Division by zero!");
                            }
                            result = operand1 / operand2;
                            break;
                        case '%':
                            result = operand1 % operand2;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid operator: " + token.charAt(0));
                    }
                    operandStack.push(result);
                }
            }

            double result = operandStack.pop();
            String res= String.valueOf(result);
            if(res.endsWith(".0"))
                res=res.replace(".0","");
            return res;
        } catch (ArithmeticException e) {
            // Handle division by zero or other arithmetic errors
            return "Err";
        } catch (IllegalArgumentException e) {
            // Handle invalid operator or other illegal argument errors
            return "Err";
        } catch (Exception e) {
            // Handle any other unexpected errors
            return "Err";
        }
    }
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}