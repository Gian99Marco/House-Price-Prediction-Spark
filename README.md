# House Price Prediction

## Table of Contents
1. [Introduction](#introduction)
2. [Dataflow and Technologies Used](#dataflow-and-technologies-used)
    1. [Dataset and Input Format](#dataset-and-input-format)
    2. [Technologies Used](#technologies-used)
    3. [Architecture and Data Flow](#architecture-and-data-flow)
3. [Use Case](#use-case)
4. [Limitations and Possible Extensions](#limitations-and-possible-extensions)
5. [How to Run](#how-to-run)
6. [Contributing](#contributing)
7. [License](#license)

## Introduction
This project aims to predict house prices using a Linear Regression model implemented with Apache Spark's MLlib. It utilizes various features of the houses, such as the overall quality, living area, garage cars, garage area, total basement area, number of full bathrooms, year built, and number of bedrooms to make accurate predictions.

## Dataflow and Technologies Used

### Dataset and Input Format
The dataset used for training the model includes the following features:
- Overall Quality
- Living Area (in square feet)
- Garage Cars
- Garage Area (in square feet)
- Total Basement Area (in square feet)
- Number of Full Bathrooms
- Year Built
- Number of Bedrooms

The dataset can be found at this link: [House Prices](https://www.kaggle.com/competitions/house-prices-advanced-regression-techniques)

### Technologies Used
- **Apache Spark**: For distributed data processing and machine learning model training.
- **Java**: The programming language used to implement the project.
- **Maven**: For project build and dependency management.

### Architecture and Data Flow
1. **Data Loading**: The dataset is loaded from a CSV file into a Spark DataFrame.
2. **Data Preprocessing**: The data is cleaned and preprocessed, handling missing values and transforming features as necessary.
3. **Feature Engineering**: Relevant features are selected and assembled into a feature vector.
4. **Model Training**: A Linear Regression model is trained using the preprocessed data.
5. **Prediction**: The model is used to make predictions on new data.

## Use Case
This project can be used by real estate companies, financial analysts, and individual buyers or sellers to estimate the price of a house based on its features. It can help in making informed decisions regarding buying or selling properties.

## Limitations and Possible Extensions
### Limitations
- The model is only as good as the data it is trained on. If the data is not representative of the real-world scenario, the predictions might not be accurate.
- The model does not account for macroeconomic factors, location-specific trends, or other external variables that can affect house prices.

### Possible Extensions
- Integrate additional features such as neighborhood crime rates, school ratings, proximity to amenities, etc.
- Experiment with other machine learning algorithms such as Decision Trees, Random Forests, or Gradient Boosting.
- Implement a web interface to achieve greater accessibility and interaction with the user.

## Requirements
- [Apache Spark 3.3.0](https://spark.apache.org/releases/spark-release-3-3-0.html)
- [Apache MLlib](https://spark.apache.org/mllib/)
- [Apache Maven 3.6.3](https://maven.apache.org/docs/3.6.3/release-notes.html)
- [Java 8](https://www.java.com/it/download/help/java8.html)

## How to Run
1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/house-price-prediction-spark.git
2. **Adjust the csv path**

    In HousePricePrediction.java replace "/home/bigdata2022/Scrivania/house-price-prediction/src/main/resources" with "/your/path/to/house-price-        prediction/src/main/resources" 

4. **Build the Project**
   ```bash
   mvn clean install
5. **Run the Application**
   ```bash
   java -cp target/house-price-prediction-1.0-SNAPSHOT.jar com.example.HousePricePrediction
   
