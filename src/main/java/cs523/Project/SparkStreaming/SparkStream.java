package cs523.Project.SparkStreaming;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.flume.*;

import scala.Tuple2;

import java.security.MessageDigest;
import java.util.Arrays;

public class SparkStream {

	private static final String TABLE_NAME = "Log";
	private static final String CF_ERROR = "ErrorLog";
	private static final String CF_INFO = "InfoLog";

	public static void main(String[] args) {
	// Create a local StreamingContext with two working thread and batch interval of 1 second
	SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("FlumeSparkStream");
	JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

		JavaReceiverInputDStream<SparkFlumeEvent> flumeStream =
			 	FlumeUtils.createStream(jssc, args[0], Integer.parseInt(args[1]));
		JavaDStream<SparkFlumeEvent> errors = flumeStream.filter(log -> log.event().toString().contains("LEVEL=ERROR"));
		JavaDStream<String> errorLog= errors.map(error -> error.event().getBody().toString());
		
		JavaDStream<SparkFlumeEvent> info = flumeStream.filter(log -> log.event().toString().contains("LEVEL=INFO"));
		JavaDStream<String> infoLog= errors.map(error -> error.event().getBody().toString());
		
		
//		jssc.checkpoint("hdfs://quickstart.cloudera:8020/sparkHbase");
//		Configuration config = HBaseConfiguration.create();
//		config.set("hbase.zookeeper.quorum", "localhost:2181");
//		try{
//			Connection connection = ConnectionFactory.createConnection(config);
//				Admin admin = connection.getAdmin();
//				HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
//				table.addFamily(new HColumnDescriptor(CF_ERROR).setCompressionType(Algorithm.NONE));
//				table.addFamily(new HColumnDescriptor(CF_INFO).setCompressionType(Algorithm.NONE));
//				System.out.print("Creating table.... ");
//
//				if (admin.tableExists(table.getTableName()))
//				{
//					admin.disableTable(table.getTableName());
//					admin.deleteTable(table.getTableName());
//				}
//				admin.createTable(table);
//				System.out.println("Table creation Done!");
//				HTable hTable = new HTable(config, Bytes.toBytes(TABLE_NAME));
//				
//				String key = errorLog.toString() + infoLog.toString();
//				MessageDigest md = MessageDigest.getInstance("SHA-512");				
//				Put p = new Put((md.digest(key.getBytes())));
//				
//				p.add(Bytes.toBytes(CF_ERROR), Bytes.toBytes("error"),Bytes.toBytes(errorLog.toString()));
//				p.add(Bytes.toBytes(CF_INFO), Bytes.toBytes("info"),Bytes.toBytes(infoLog.toString()));
//				hTable.put(p);
//				
//		} catch(Exception ex) {
//			System.out.println("Error:" + ex.getMessage());
//		}
		
		infoLog.print();
		errorLog.print();
		 jssc.start();              // Start the computation
		 jssc.awaitTermination();   // Wait for the computation to terminate		
	}
}
