package com.example.mipt3_calc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private EditText display;
    private String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        input += buttonText;
        display.setText(input);
    }

    public void onOperatorClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        if (input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/")) {
            input = input.substring(0, input.length() - 1);
        }

        input += buttonText;
        display.setText(input);
    }

    public void onEqualsClick(View view) {
        try {
            double result = evaluate(input);
            display.setText(formatResult(result));
            input = String.valueOf(result);
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    public void onClearClick(View view) {
        input = "";
        display.setText("");
    }

    public void onClearEntryClick(View view) {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            display.setText(input);
        }
    }

    public void onBackspaceClick(View view) {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            display.setText(input);
        }
    }

    public void onPercentClick(View view) {
        if (!input.isEmpty()) {
            try {
                double value = Double.parseDouble(input);
                value /= 100.0;
                input = String.valueOf(value);
                display.setText(formatResult(value));
            } catch (NumberFormatException e) {
                display.setText("Error");
            }
        }
    }

    public void onSquareRootClick(View view) {
        if (!input.isEmpty()) {
            try {
                double value = Double.parseDouble(input);
                if (value >= 0) {
                    value = Math.sqrt(value);
                    input = String.valueOf(value);
                    display.setText(formatResult(value));
                } else {
                    display.setText("Error");
                }
            } catch (NumberFormatException e) {
                display.setText("Error");
            }
        }
    }

    public void onPlusMinusClick(View view) {
        if (!input.isEmpty()) {
            try {
                double value = Double.parseDouble(input);
                value = -value;
                input = String.valueOf(value);
                display.setText(formatResult(value));
            } catch (NumberFormatException e) {
                display.setText("Error");
            }
        }
    }

    public void onDecimalClick(View view) {
        if (!input.endsWith(".") && input.length() > 0) {
            input += ".";
            display.setText(input);
        }
    }

    public void onInverseClick(View view) {
        if (!input.isEmpty()) {
            try {
                double value = Double.parseDouble(input);
                if (value != 0) {
                    value = 1 / value;
                    input = String.valueOf(value);
                    display.setText(formatResult(value));
                } else {
                    display.setText("Error");
                }
            } catch (NumberFormatException e) {
                display.setText("Error");
            }
        }
    }

    private double evaluate(String expression) {
        // Remove any leading or trailing whitespace from the expression
        expression = expression.trim();

        try {
            // Create two stacks: one for numbers and one for operators
            Stack<Double> numbers = new Stack<>();
            Stack<Character> operators = new Stack<>();

            // Iterate through each character in the expression
            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                // Skip whitespace
                if (c == ' ') {
                    continue;
                }

                // If the character is a digit, extract the number
                if (Character.isDigit(c) || (c == '.')) {
                    StringBuilder numBuilder = new StringBuilder();
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        numBuilder.append(expression.charAt(i));
                        i++;
                    }
                    i--; // Move the index back to the last digit
                    double number = Double.parseDouble(numBuilder.toString());
                    numbers.push(number);
                } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                    // Process operators (+, -, *, /)
                    while (!operators.isEmpty() && hasPrecedence(operators.peek(), c)) {
                        double b = numbers.pop();
                        double a = numbers.pop();
                        char operator = operators.pop();
                        double result = applyOperator(a, b, operator);
                        numbers.push(result);
                    }
                    operators.push(c);
                } else if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        double b = numbers.pop();
                        double a = numbers.pop();
                        char operator = operators.pop();
                        double result = applyOperator(a, b, operator);
                        numbers.push(result);
                    }
                    operators.pop(); // Remove the '(' from the stack
                }
            }

            // Process any remaining operators
            while (!operators.isEmpty()) {
                double b = numbers.pop();
                double a = numbers.pop();
                char operator = operators.pop();
                double result = applyOperator(a, b, operator);
                numbers.push(result);
            }

            // The final result is on top of the 'numbers' stack
            if (!numbers.isEmpty()) {
                return numbers.pop();
            } else {
                throw new ArithmeticException("Invalid expression");
            }
        } catch (ArithmeticException e) {
            return 0; // Return 0 for invalid expressions
        }
    }

    private boolean hasPrecedence(char operator1, char operator2) {
        if ((operator1 == '+' || operator1 == '-') && (operator2 == '*' || operator2 == '/')) {
            return false;
        }
        return true;
    }

    private double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b != 0) {
                    return a / b;
                } else {
                    throw new ArithmeticException("Division by zero");
                }
            default:
                return 0;
        }
    }


    private String formatResult(double result) {
        // Format the result to a user-friendly string
        DecimalFormat df = new DecimalFormat("#.#####"); // You can adjust the formatting
        return df.format(result);
    }
}
