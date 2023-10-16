package com.example.moblab1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText operand1EditText;
    private EditText operand2EditText;
    private Spinner operatorSpinner;
    private Spinner measurementSystemSpinner;
    private TextView resultTextView;

    private int prevBase = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setListeners();
        restoreSavedValues();
    }

    private void initializeViews() {
        operand1EditText = findViewById(R.id.editTextOperand1);
        operand2EditText = findViewById(R.id.editTextOperand2);
        operatorSpinner = findViewById(R.id.spinnerOperator);
        measurementSystemSpinner = findViewById(R.id.measurementSystemSpinner);
        resultTextView = findViewById(R.id.resultTextView);
    }

    private void setListeners() {
        measurementSystemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBase = measurementSystemSpinner.getSelectedItem().toString();
                updateOperandValues(selectedBase);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected.
            }
        });

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculateResult());

        Button navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }

    private void restoreSavedValues() {
        operand1EditText.setText(getSavedValue("operand1"));
        operand2EditText.setText(getSavedValue("operand2"));

        String savedOperator = getSavedValue("operator");
        int operatorPosition = ((ArrayAdapter<String>) operatorSpinner.getAdapter()).getPosition(savedOperator);
        operatorSpinner.setSelection(operatorPosition);

        String savedBase = getSavedValue("baseType");
        int basePosition = ((ArrayAdapter<String>) measurementSystemSpinner.getAdapter()).getPosition(savedBase);
        measurementSystemSpinner.setSelection(basePosition);
        prevBase = convertBaseStringToInt(savedBase);
    }

    private String getSavedValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void saveValue(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void calculateResult() {
        String operand1Str = operand1EditText.getText().toString();
        String operand2Str = operand2EditText.getText().toString();
        String operator = operatorSpinner.getSelectedItem().toString();
        String selectedMeasurementSystem = measurementSystemSpinner.getSelectedItem().toString();

        saveValue("operand1", operand1Str);
        saveValue("operand2", operand2Str);
        saveValue("operator", operator);
        saveValue("baseType", selectedMeasurementSystem);

        StringBuilder resultStrBuilder = new StringBuilder();

        try {
            int operand1 = Integer.parseInt(operand1Str, prevBase);
            int operand2 = Integer.parseInt(operand2Str, prevBase);
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
                        resultStrBuilder.append("Cannot divide by zero.\n");
                        break;
                    }
                    break;
                default:
                    resultStrBuilder.append("Invalid operator.\n");
                    break;
            }

            for (int base : new int[]{2, 8, 10, 16}) {
                String resultInBase = Integer.toString(result, base);
                resultStrBuilder.append("Result in base ").append(base).append(": ").append(resultInBase).append("\n");
            }
        } catch (NumberFormatException e) {
            resultStrBuilder.append("Invalid operands.");
        }

        resultTextView.setText(resultStrBuilder.toString());
    }

    @SuppressLint("SetTextI18n")
    private void updateOperandValues(String selectedBase) {
        int base = convertBaseStringToInt(selectedBase);

        String operand1Str = operand1EditText.getText().toString();
        String operand2Str = operand2EditText.getText().toString();

        try {
            int operand1 = Integer.parseInt(operand1Str, prevBase);
            int operand2 = Integer.parseInt(operand2Str, prevBase);
            operand1EditText.setText(Integer.toString(operand1, base));
            operand2EditText.setText(Integer.toString(operand2, base));
        } catch (NumberFormatException e) {
            operand1EditText.setText("-");
            operand2EditText.setText("-");
        }
        prevBase = base;
    }

    private int convertBaseStringToInt(String baseString) {
        int base = prevBase;
        switch (baseString) {
            case "Base 2":
                base = 2;
                break;
            case "Base 8":
                base = 8;
                break;
            case "Base 10":
                base = 10;
                break;
            case "Base 16":
                base = 16;
                break;
        }
        return base;
    }
}
