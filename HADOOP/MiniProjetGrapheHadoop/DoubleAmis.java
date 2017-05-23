import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DoubleAmis {

	/* permet de mapper les donnees en entrees */
	/* mapper <entree, entree, sortie, sortie> */
	public static class EtendreMapper extends Mapper<Object, Text, Text, Text> {

		private String tmpClef = "";
		private String tmpValeur = "";

		/*
		 * context : c'est la que l'on dit ce que l'on va resortir (dans le
		 * context)
		 */
		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] tmp = value.toString().split(" ");
			for (int i = 1; i < tmp.length; i++) {
				tmpClef = "";
				tmpValeur = "";
				// on veut avoir des clef qui sont toujours les meme (0.1 est la
				// meme que 1,0)
				if (Integer.parseInt(tmp[0]) < Integer.parseInt(tmp[i])) {
					tmpClef = tmp[0];
					tmpClef += " ";
					tmpClef += tmp[i];
				} else {
					tmpClef = tmp[i];
					tmpClef += " ";
					tmpClef += tmp[0];
				}

				// on va rajouter tous les amis du couple
				for (int y = 1; y < tmp.length; y++) {
					if (y != i) {
						tmpValeur += tmp[y] + " ";
					}
				}
				context.write(new Text(tmpClef), new Text(tmpValeur));
			}

		}
	}

	/* reducer */
	/* en entree : mot, ocurence du mot. En sortie pareil */
	/* c'est toujours dans le context que l'on gere cela */

	/* Reducer <entree, entree, sortie, sortie> */

	public static class InterseptionReducer extends
			Reducer<Text, Text, Text, Text> {
		// private Text result = new Text();
		// private String result = null;
		private String tmp = "";
		private int nbDeConnaissanceEnCommun;

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			tmp = "";
			ArrayList<String> resultOne = new ArrayList<String>();

			Iterator<Text> ite = values.iterator();

			String tmpOne = ite.next().toString();
			String tmpTwo = ite.next().toString();

			for (String str : tmpOne.split(" ")) {
				resultOne.add(str);
			}

			this.nbDeConnaissanceEnCommun = 0;

			for (String str : tmpTwo.split(" ")) {

				if (resultOne.contains(str)) {
					tmp += str + " ";
					this.nbDeConnaissanceEnCommun++;
				}
			}

			tmp = "(" + this.nbDeConnaissanceEnCommun + ") " + tmp;

			context.write(key, new Text(tmp));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "GPS");
		/* le driver */
		job.setJarByClass(DoubleAmis.class);

		/* celui qui va faire le mapper */
		/* en entree un morceau de donne, en sortie une map clef valeur */
		job.setMapperClass(EtendreMapper.class);

		// job.setCombinerClass(IntSumReducer.class);

		job.setReducerClass(InterseptionReducer.class);

		job.setOutputKeyClass(Text.class);

		/* type */
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));

		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		/* c ici que l'on attend que le jbo finisse */
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}
}