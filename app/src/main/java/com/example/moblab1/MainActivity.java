package com.example.moblab1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText operand1EditText;
    private EditText operand2EditText;
    private Spinner operatorSpinner;
    private Spinner measurementSystemSpinner; // Add Spinner for measurement system
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operand1EditText = findViewById(R.id.editTextOperand1);
        operand2EditText = findViewById(R.id.editTextOperand2);
        operatorSpinner = findViewById(R.id.spinnerOperator);
        measurementSystemSpinner = findViewById(R.id.measurementSystemSpinner);
        resultTextView = findViewById(R.id.resultTextView);

        // Retrieve and set operand values from SharedPreferences
        operand1EditText.setText(getOperandValue("operand1"));
        operand2EditText.setText(getOperandValue("operand2"));

        // Retrieve and set selected operator value from SharedPreferences
        String savedOperator = getOperatorValue("operator");
        int operatorPosition = ((ArrayAdapter<String>) operatorSpinner.getAdapter()).getPosition(savedOperator);
        operatorSpinner.setSelection(operatorPosition);

        // Retrieve and set selected base (measurement system) value from SharedPreferences
        String savedBase = getBaseValue("baseType");
        int basePosition = ((ArrayAdapter<String>) measurementSystemSpinner.getAdapter()).getPosition(savedBase);
        measurementSystemSpinner.setSelection(basePosition);

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

        // Populate the measurement system Spinner
        ArrayAdapter<CharSequence> measurementSystemAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.measurement_systems,
                android.R.layout.simple_spinner_item
        );
        measurementSystemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementSystemSpinner.setAdapter(measurementSystemAdapter);

    }

    private String getOperatorValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private String getBaseValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void saveToLocal(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getOperandValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void calculateResult() {
        String operand1Str = operand1EditText.getText().toString();
        String operand2Str = operand2EditText.getText().toString();
        String operator = operatorSpinner.getSelectedItem().toString();
        String selectedMeasurementSystem = measurementSystemSpinner.getSelectedItem().toString();

        // Save operand, operator, and base (measurement system) values to SharedPreferences
        saveToLocal("operand1", operand1Str);
        saveToLocal("operand2", operand2Str);
        saveToLocal("operator", operator); // Save selected operator
        saveToLocal("baseType", selectedMeasurementSystem); // Save selected base (measurement system)


        String resultStr;

        switch (selectedMeasurementSystem) {
            case "Base 2":
                resultStr = calculate(operand1Str, operand2Str, operator, 2);
                break;
            case "Base 8":
                resultStr = calculate(operand1Str, operand2Str, operator, 8);
                break;
            case "Base 10":
                resultStr = calculate(operand1Str, operand2Str, operator, 10);
                break;
            case "Base 16":
                resultStr = calculate(operand1Str, operand2Str, operator, 16);
                break;
            default:
                resultTextView.setText("Invalid measurement system.");
                return;
        }

        resultTextView.setText("Result: " + resultStr);
    }

    private String calculate(String operand1Str, String operand2Str, String operator, int base) {
        try {
            // Parse operands based on the selected base
            int operand1 = Integer.parseInt(operand1Str, base);
            int operand2 = Integer.parseInt(operand2Str, base);
            int result = 0;

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
                        return "Cannot divide by zero.";
                    }
                    break;
                default:
                    return "Invalid operator.";
            }

            // Convert the result back to a string in the selected base
            return Integer.toString(result, base);
        } catch (NumberFormatException e) {
            return "Invalid operands.";
        }
    }
}
