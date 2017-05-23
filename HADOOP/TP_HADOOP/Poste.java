import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Poste {
	/**
	 * @author AFRASS, TAIBI
	 *
	 */
	public static class codePostalMapper extends
			Mapper<Object, Text, Text, Text> {

		private Text ville = new Text();
		private Text latLong = new Text();

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			/* Split de l'entree */
			String[] itr = value.toString().split(";", -2);
			/*
			 * information ville en case 8 lat en case 48... long 2...
			 */

			/* set de clef */

			/* set valeur */
			ville.set(itr[8]);

			latLong.set(itr[10]/* + " - " + itr[11] */);

			context.write(ville, latLong);
		}
	}

	public static class CodeReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String sum = "";
			for (Text val : values) {
				sum += val.toString() + " ";
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Poste");

		job.setJarByClass(Poste.class);
		job.setMapperClass(codePostalMapper.class);
		job.setCombinerClass(CodeReducer.class);
		job.setReducerClass(CodeReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}