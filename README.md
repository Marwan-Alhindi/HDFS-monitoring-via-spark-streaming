# Spark Streaming Application for HDFS Monitoring

## Overview

This project implements a real-time HDFS monitoring system using Apache Spark Streaming. The objective is to continuously monitor a folder on the HDFS for new files and process them in real-time using Scala. This project consists of three tasks implemented within a single Scala object:

### Task A:
For each RDD in the DStream, the program counts the word frequency and saves the output to HDFS.

### Task B:
Processes each word in the DStream similarly to Task A, but also counts the co-occurrence frequency of words that appear in the same line, saving the results to HDFS.

### Task C:
Updates the co-occurrence frequency of words in the DStream continuously using the `updateStateByKey` operation and saves the results to HDFS.

## Files

- **application.jar**: The compiled JAR file for the Spark Streaming application.
- **code.scala**: Scala source code for the application, containing all three tasks (A, B, and C).
- **README.md**: Instructions on how to run the application.

## Prerequisites

- AWS CLI installed and configured.
- Apache Spark 3.x installed.
- Scala 2.x installed.

## Steps to Run the Application on AWS EMR

1. **Transfer Files to Jumphost**:
   ```bash
   scp -i "path/to/your/private/key" /path/to/local/files user@remote_host:/path/to/remote/directory
   ```

   Example:
   ```bash
   scp -i "/Users/marwan/Desktop/Big Data/s3969393-cosc2637.pem" /Users/marwan/Desktop/s3969393_BDP_A3/application.jar ec2-user@s3969393.jump.cosc2637.route53.aws.rmit.edu.au:/home/ec2-user
   ```

2. **Access Jumphost**:
   ```bash
   ssh jumphost
   ```

3. **Create a Cluster**:
   ```bash
   ./create_cluster.sh
   ```

4. **Transfer Files to HDFS**:
   ```bash
   scp -i "path/to/your/private/key" application.jar hadoop@s3969393.emr.cosc2637.route53.aws.rmit.edu.au:/home/hadoop
   ```

5. **Connect to HDFS**:
   - Retrieve the command to access HDFS: 
     ```bash
     cat instructions
     ```
   - Connect to HDFS: 
     ```bash
     ssh hadoop@s3969393.emr.cosc2637.route53.aws.rmit.edu.au -i s3969393-cosc2637.pem
     ```

6. **Create Input Directory on HDFS**:
   ```bash
   hdfs dfs -mkdir -p /File/mon/monitor/
   ```

7. **Get the Master URL**:
   ```bash
   hdfs getconf -confKey yarn.resourcemanager.address
   ```

8. **Run the JAR Application**:
   ```bash
   spark-submit --class stream_processor.StreamProcessor --master spark://ip-192-168-27-90.ec2.internal:8032 application.jar /File/mon/monitor/ /File/mon/output/
   ```

9. **Manage Concurrent Applications on HDFS** (if necessary):
   - List running applications:
     ```bash
     yarn application -list
     ```
   - Terminate an application:
     ```bash
     yarn application -kill <application_id>
     ```

10. **Open a New Terminal Session**.

11. **Access Jumphost and HDFS Again**.

12. **Create a Text File for Processing**:
    ```bash
    nano textFileName.txt
    ```
    Insert content, then save and exit.

13. **Transfer the Text File to HDFS**:
    ```bash
    hdfs dfs -put textFileName.txt /File/mon/monitor/
    ```

14. **Inspect Output Files**:
    ```bash
    hdfs dfs -ls /File/mon/output/
    ```

15. **Merge Output Files**:
    ```bash
    hdfs dfs -getmerge /File/mon/output/(name of output directory) / merged_output_(name of output directory).txt
    ```

16. **View the Output**:
    ```bash
    cat merged_output_taskA-001.txt
    ```

## Tasks Summary

1. **Task A**: Count word frequency for each RDD in the DStream and save the output.
2. **Task B**: Count the co-occurrence frequency of words for each RDD, processing words in the same line.
3. **Task C**: Continuously update co-occurrence frequency using the `updateStateByKey` operation.
