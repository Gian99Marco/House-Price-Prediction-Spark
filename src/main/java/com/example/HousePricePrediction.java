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

import java.util.Arrays;
import java.util.Scanner;

public class HousePricePrediction {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("HousePricePrediction")
                .master("local[*]")
                .getOrCreate();

        Dataset<Row> dataset = spark.read().format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load("/home/bigdata2022/Scrivania/house-price-prediction/src/main/resources");

        // Drop rows with null values
        dataset = dataset.na().drop(new String[]{"OverallQual", "GrLivArea", "GarageCars", "GarageArea", "TotalBsmtSF", "FullBath", "YearBuilt", "BedroomAbvGr"});

        dataset = dataset.withColumnRenamed("OverallQual", "overallQual")
                .withColumnRenamed("GrLivArea", "grLivArea")
                .withColumnRenamed("GarageCars", "garageCars")
                .withColumnRenamed("GarageArea", "garageArea")
                .withColumnRenamed("TotalBsmtSF", "totalBsmtSF")
                .withColumnRenamed("FullBath", "fullBath")
                .withColumnRenamed("YearBuilt", "yearBuilt")
                .withColumnRenamed("BedroomAbvGr", "bedroomAbvGr")
                .withColumnRenamed("SalePrice", "salePrice");

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"overallQual", "grLivArea", "garageCars", "garageArea", "totalBsmtSF", "fullBath", "yearBuilt", "bedroomAbvGr"})
                .setOutputCol("features")
                .setHandleInvalid("skip"); // Handle invalid entries

        LinearRegression lr = new LinearRegression()
                .setLabelCol("salePrice")
                .setFeaturesCol("features");

        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{assembler, lr});

        PipelineModel model = pipeline.fit(dataset);

        Scanner scanner = new Scanner(System.in);
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

        Row inputRow = RowFactory.create(overallQual, grLivArea, garageCars, garageArea, totalBsmtSF, fullBath, yearBuilt, bedroomAbvGr);
        Dataset<Row> inputData = spark.createDataFrame(Arrays.asList(inputRow), schema);

        Dataset<Row> predictions = model.transform(inputData);
        predictions.select("prediction").show();

        spark.stop();
    }
}

