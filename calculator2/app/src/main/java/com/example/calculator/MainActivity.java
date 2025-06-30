package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView resultView;
    String input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = findViewById(R.id.result);
    }

    public void numberClick(View view) {
        input += ((TextView) view).getText().toString();
        resultView.setText(input);
    }

    public void operatorClick(View view) {
        String op = ((TextView) view).getText().toString();
        input += op.equals("×") ? "*" : op.equals("÷") ? "/" : op;
        resultView.setText(input);
    }

    public void clear(View view) {
        input = "";
        resultView.setText("0");
    }

    public void delete(View view) {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            resultView.setText(input.isEmpty() ? "0" : input);
        }
    }

    public void calculate(View view) {
        try {
            double result = eval(input);
            resultView.setText(String.valueOf(result));
            input = String.valueOf(result);
        } catch (Exception e) {
            resultView.setText("Error");
        }
    }

    // Basic expression evaluator
    public double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
}
