package com.example.moblab1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {

    private EditText operand1EditText;
    private EditText operand2EditText;
    private Spinner operatorSpinner;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operand1EditText = findViewById(R.id.editTextOperand1);
        operand2EditText = findViewById(R.id.editTextOperand2);
        operatorSpinner = findViewById(R.id.spinnerOperator);
        resultTextView = findViewById(R.id.resultTextView);

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });

        Button navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        // Create an Intent to start the SecondActivity
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
});
    }

    private void calculateResult() {
        double operand1 = Double.parseDouble(operand1EditText.getText().toString());
        double operand2 = Double.parseDouble(operand2EditText.getText().toString());
        String operator = operatorSpinner.getSelectedItem().toString();

        double result = 0;

        switch (operator) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            case "/":
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    resultTextView.setText("Cannot divide by zero.");
                    return;
                }
                break;
            default:
                resultTextView.setText("Invalid operator.");
                return;
        }

        resultTextView.setText("Result: " + result);
    }

}