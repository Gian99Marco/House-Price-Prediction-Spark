package com.example;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

public class HousePricePrediction {
    public static void main(String[] args) {
        // Create a Spark session
        SparkSession spark = SparkSession.builder()
                .appName("HousePricePrediction")
                .master("local[*]") // Use all available cores
                .getOrCreate();

        // Load the dataset from a CSV file with headers and infer the schema
        Dataset<Row> dataset = spark.read().format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load("/home/bigdata2022/Scrivania/house-price-prediction/src/main/resources");

        // Drop rows with null values in the specified columns
        dataset = dataset.na().drop(new String[]{"OverallQual", "GrLivArea", "GarageCars", "GarageArea", "TotalBsmtSF", "FullBath", "YearBuilt", "BedroomAbvGr"});

        // Rename columns to follow camelCase naming convention
        dataset = dataset.withColumnRenamed("OverallQual", "overallQual")
                .withColumnRenamed("GrLivArea", "grLivArea")
                .withColumnRenamed("GarageCars", "garageCars")
                .withColumnRenamed("GarageArea", "garageArea")
                .withColumnRenamed("TotalBsmtSF", "totalBsmtSF")
                .withColumnRenamed("FullBath", "fullBath")
                .withColumnRenamed("YearBuilt", "yearBuilt")
                .withColumnRenamed("BedroomAbvGr", "bedroomAbvGr")
                .withColumnRenamed("SalePrice", "salePrice");

        // Assemble feature columns into a single vector column
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"overallQual", "grLivArea", "garageCars", "garageArea", "totalBsmtSF", "fullBath", "yearBuilt", "bedroomAbvGr"})
                .setOutputCol("features")
                .setHandleInvalid("skip"); // Handle invalid entries by skipping them

        // Create a linear regression model
        LinearRegression lr = new LinearRegression()
                .setLabelCol("salePrice")
                .setFeaturesCol("features");

        // Create a pipeline to chain data transformations and model training
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{assembler, lr});

        // Train the model using the dataset
        PipelineModel model = pipeline.fit(dataset);

        // Use a scanner to read input values from the user
        try (Scanner scanner = new Scanner(System.in)) {
            // Prompt the user for input values for each feature
            System.out.print("Enter Overall Quality (1-10): ");
            double overallQual = scanner.nextDouble();
            System.out.print("Enter Above Ground Living Area (sq ft): ");
            double grLivArea = scanner.nextDouble();
            System.out.print("Enter Number of Garage Cars: ");
            double garageCars = scanner.nextDouble();
            System.out.print("Enter Garage Area (sq ft): ");
            double garageArea = scanner.nextDouble();
            System.out.print("Enter Total Basement Area (sq ft): ");
            double totalBsmtSF = scanner.nextDouble();
            System.out.print("Enter Number of Full Bathrooms: ");
            double fullBath = scanner.nextDouble();
            System.out.print("Enter Year Built: ");
            double yearBuilt = scanner.nextDouble();
            System.out.print("Enter Number of Bedrooms Above Ground: ");
            double bedroomAbvGr = scanner.nextDouble();

            // Define the schema for the input data
            StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("overallQual", DataTypes.DoubleType, false),
                DataTypes.createStructField("grLivArea", DataTypes.DoubleType, false),
                DataTypes.createStructField("garageCars", DataTypes.DoubleType, false),
                DataTypes.createStructField("garageArea", DataTypes.DoubleType, false),
                DataTypes.createStructField("totalBsmtSF", DataTypes.DoubleType, false),
                DataTypes.createStructField("fullBath", DataTypes.DoubleType, false),
                DataTypes.createStructField("yearBuilt", DataTypes.DoubleType, false),
                DataTypes.createStructField("bedroomAbvGr", DataTypes.DoubleType, false)
            });

            // Create a row with the input values
            Row inputRow = RowFactory.create(overallQual, grLivArea, garageCars, garageArea, totalBsmtSF, fullBath, yearBuilt, bedroomAbvGr);
            // Convert the row to a dataset
            Dataset<Row> inputData = spark.createDataFrame(Arrays.asList(inputRow), schema);

            // Use the trained model to make predictions on the input data
            Dataset<Row> predictions = model.transform(inputData);
            // Select the prediction column
            Dataset<Row> predictionResult = predictions.select("prediction");

            // Format the prediction result to two decimal places
            DecimalFormat df = new DecimalFormat("#.00");
            predictionResult.foreach(row -> {
                double prediction = row.getDouble(0);
                String formattedPrediction = df.format(prediction);
                String message = "Predicted House Price: $" + formattedPrediction;

                // Calculate the width of the rectangle
                int width = message.length() + 4;
                StringBuilder border = new StringBuilder();
                for (int i = 0; i < width; i++) {
                    border.append("*");
                }

                // Print the rectangle with the message inside
                System.out.println(border);
                System.out.println("* " + message + " *");
                System.out.println(border);
            });
        }
        // Stop the Spark session
        spark.stop();
    }
}
