Name: Marwan Alhandi  
Student ID: s3969393

Instructions for Running the Application and Processing Data:

1. Transfer Files to Jumphost:
   - General Format: `scp -i "path/to/your/private/key" /path/to/local/files user@remote_host:/path/to/remote/directory`
   - Example: `scp -i "/Users/marwan/Desktop/Big Data/s3969393-cosc2637.pem" /Users/marwan/Desktop/s3969393_BDP_A3/application.jar ec2-user@s3969393.jump.cosc2637.route53.aws.rmit.edu.au:/home/ec2-user`

2. Access Jumphost:
   - Execute `ssh jumphost`.

3. Create a Cluster:
   - Run `./create_cluster.sh`.

4. Transfer Files to HDFS:
   - General Format: `scp -i "path/to/your/private/key" local_files user@remote_host:path/to/remote/directory`
   - Example: `scp -i "/home/ec2-user/s3969393-cosc2637.pem" application.jar hadoop@s3969393.emr.cosc2637.route53.aws.rmit.edu.au:/home/hadoop`
   - Confirm the prompt by clicking 'yes'.

5. Connect to HDFS:
   - Retrieve the command to access HDFS: `cat instructions`
   - Connect to HDFS: `ssh hadoop@s3969393.emr.cosc2637.route53.aws.rmit.edu.au -i s3969393-cosc2637.pem`

6. Create an Input Directory for the Application:
   - Execute `hdfs dfs -mkdir -p /File/mon/monitor/`.

7. Get the master url:
- Execture 'hdfs getconf -confKey yarn.resourcemanager.address'

8. Execute the JAR Application:
   - Run the following command and wait until the status changes from 'ACCEPTED' to 'RUNNING' (modify input and output paths as necessary):
   - Make sure to replace the url starting from the ip:

     ```
	spark-submit --class stream_processor.StreamProcessor --master spark://ip-192-168-27-90.ec2.internal:8032 application.jar /File/mon/monitor/ /File/mon/output/
     ```
	 
9. Manage Concurrent Applications on HDFS (Not necessary if JAR application executed successfully):
   - List running applications: `yarn application -list`
   - Terminate an application: `yarn application -kill <application_id>`
   - After terminating conflicting applications, rerun the JAR application.

10. Open a New Terminal Session.

11. Access Jumphost and HDFS Again.

12. Create a Text File for Processing:
    - Open a new file with `nano textFileName.txt`.
    - Insert the content to be processed.
    - Save and exit by pressing `Ctrl + X`, then press `Y`.

13. Transfer the Text File to the Input Directory:
    - Execute `hdfs dfs -put textFileName.txt /File/mon/monitor/`.

14. Inspect All Output Files:
    - hdfs dfs -ls /File/mon/output/

15. View specific output directory:
    - hdfs dfs -ls /File/mon/output/(name of output directory)
    - Example: 'hdfs dfs -ls /File/mon/output/taskA-001'


15. Consolidate Output File:
    - Merge files using `hdfs dfs -getmerge /File/mon/output/(name of output directory) / merged_output_(name of output directory).txt`.
    - Example: `hdfs dfs -getmerge /File/mon/output/taskA-001 / merged_output_taskA-001.txt`.

16. Display the Output:
    - View the content with `cat merged_output_(name of the output directory).txt`.
    - Example: `cat merged_output_taskA-001.txt`.