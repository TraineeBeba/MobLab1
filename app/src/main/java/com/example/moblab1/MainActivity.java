package com.example.moblab1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {



    // Create the object of TextView and PieChart class
    TextView tvR, tvPython, tvCPP, tvJava;
    PieChart pieChart;
    HashMap<String, Integer> operatorUsage = new HashMap<>();
    HashMap<String, String> savedValues = new HashMap<>();

    File file;
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

        initFile();
        initializeViews();
        setListeners();
        restoreSavedValues();
    }

    public void initFile(){
        File directory = Environment.getExternalStorageDirectory();
        file = new File(directory,"data");
        if (!file.exists()) {
            try {
                if (Build.VERSION.SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(getpermission);
                    }
                }
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void saveValues(HashMap<String, String> values) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            for (Map.Entry<String, String> entry : values.entrySet()) {
                osw.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }

            osw.flush();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getSavedValues() {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    savedValues.put(parts[0], parts[1]);
                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedValues;
    }

    private void initializeViews() {
        pieChart = findViewById(R.id.piechart);
        pieChart.startAnimation();
        operatorUsage.put("+", 0);
        operatorUsage.put("-", 0);
        operatorUsage.put("*", 0);
        operatorUsage.put("/", 0);

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
        getSavedValues();
        operand1EditText.setText(savedValues.get("operand1"));
        operand2EditText.setText(savedValues.get("operand2"));

        String savedOperator = savedValues.get("operator");
        int operatorPosition = ((ArrayAdapter<String>) operatorSpinner.getAdapter()).getPosition(savedOperator);
        operatorSpinner.setSelection(operatorPosition);

        String savedBase = savedValues.get("baseType");
        int basePosition = ((ArrayAdapter<String>) measurementSystemSpinner.getAdapter()).getPosition(savedBase);
        measurementSystemSpinner.setSelection(basePosition);
        prevBase = convertBaseStringToInt(savedBase);
        System.out.println(savedValues.get("operand1"));
        System.out.println(savedValues.get("operand2"));
        System.out.println(savedValues.get("operator"));
        System.out.println(savedValues.get("baseType"));
    }

    private void saveValuesToHashMap(String key, String value) {
        // Update the HashMap with the new key-value pair
        savedValues.put(key, value);
    }

    private void calculateResult() {
        String operand1Str = operand1EditText.getText().toString();
        String operand2Str = operand2EditText.getText().toString();
        String operator = operatorSpinner.getSelectedItem().toString();
        String selectedMeasurementSystem = measurementSystemSpinner.getSelectedItem().toString();

        saveValuesToHashMap("operand1", operand1Str);
        saveValuesToHashMap("operand2", operand2Str);
        saveValuesToHashMap("operator", operator);
        saveValuesToHashMap("baseType", selectedMeasurementSystem);


        saveValues(savedValues);

        System.out.println("HashMap");
        System.out.println(savedValues.toString());
        StringBuilder resultStrBuilder = new StringBuilder();

        try {
            int operand1 = Integer.parseInt(operand1Str, prevBase);
            int operand2 = Integer.parseInt(operand2Str, prevBase);
            int result = 0;

            switch (operator) {
                case "+":
                    result = operand1 + operand2;
                    operatorUsage.put("+", operatorUsage.get("+") + 1);
                    break;
                case "-":
                    result = operand1 - operand2;
                    operatorUsage.put("-", operatorUsage.get("-") + 1);
                    break;
                case "*":
                    result = operand1 * operand2;
                    operatorUsage.put("*", operatorUsage.get("*") + 1);
                    break;
                case "/":
                    if (operand2 != 0) {
                        result = operand1 / operand2;
                        operatorUsage.put("/", operatorUsage.get("/") + 1);
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
        refreshPie();
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

    void refreshPie(){
        pieChart.clearChart();
        for (String operator : operatorUsage.keySet()) {
            pieChart.addPieSlice(new PieModel(operator, operatorUsage.get(operator), getColorForOperator(operator)));
        }
    }

    private int getColorForOperator(String operator) {
        switch (operator) {
            case "+":
                return Color.parseColor("#FFA726");  // Orange
            case "-":
                return Color.parseColor("#66BB6A");  // Green
            case "*":
                return Color.parseColor("#EF5350");  // Red
            case "/":
                return Color.parseColor("#29B6F6");  // Blue
            default:
                return Color.BLACK;
        }
    }
}
