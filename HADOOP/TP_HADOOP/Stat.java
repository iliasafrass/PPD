import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Stat {
	/**
	 * @author AFRASS TAIBI
	 *
	 */
	public static class ageSalaireMapper extends
			Mapper<Object, Text, IntWritable, FloatWritable> {

		private IntWritable age;
		private FloatWritable salaire = new FloatWritable();

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			/* Split de l'entree */
			String[] itr = value.toString().split(",", -2);

			age = new IntWritable(Integer.parseInt(itr[1]));

			salaire.set(Float.parseFloat(itr[4]));

			context.write(age, salaire);
		}
	}

	public static class StatReducer extends
			Reducer<IntWritable, FloatWritable, IntWritable, Text> {
		private Text result = new Text();

		@Override
		public void reduce(IntWritable key, Iterable<FloatWritable> values,
				Context context) throws IOException, InterruptedException {
			String res = "";

			float min = 0, max = 0, average = 0, sum = 0, sumcube = 0;
			double ecart;
			int cpt = 0;
			boolean begin = true;

			for (FloatWritable v : values) {
				if (begin == true) {
					max = v.get();
					min = v.get();
					begin = false;
				}

				if (v.get() < min) {
					min = v.get();
				}
				if (v.get() > max) {
					max = v.get();
				}
				sum += v.get();
				sumcube += Math.pow(v.get(), 2);
				cpt++;
			}

			// v(x)=((1/n)sum(xi²)*m²)
			average = sum / cpt;
			ecart = ((1 / cpt) * sumcube * Math.pow(average, 2));
			String tmp = "Nombre personne %d, Salaire min %f, Salaire max %f Salaire moyen %f, ecart type %f";
			res = String.format(tmp, cpt, min, max, average, ecart);
			result.set(res);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Stat");

		job.setJarByClass(Stat.class);

		job.setMapperClass(ageSalaireMapper.class);

		// job.setCombinerClass(StatReducer.class);
		job.setReducerClass(StatReducer.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(FloatWritable.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}