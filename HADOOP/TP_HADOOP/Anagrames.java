import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Anagrames {

	/**
	 * transforme du texte en (trieParLettre(mot),mot)
	 *
	 * @author AFRASS TAIBI
	 *
	 */
	public static class WordMapper extends Mapper<Object, Text, Text, Text> {

		private Text trie = new Text();
		private Text word = new Text();

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				/* set valeur */
				word.set(itr.nextToken());

				/* créer la clef */
				LinkedList<Character> tmp = new LinkedList<>();

				for (int i = 0; i < word.getLength(); i++) {
					tmp.add((char) word.charAt(i));
				}
				Collections.sort(tmp);

				/* set de clef */
				trie.set(tmp.toString());

				context.write(trie, word);
			}
		}
	}

	public static class WordSumReducer extends Reducer<Text, Text, Text, Text> {
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
		Job job = Job.getInstance(conf, "anagrames");

		job.setJarByClass(Anagrames.class);
		job.setMapperClass(WordMapper.class);
		job.setCombinerClass(WordSumReducer.class);
		job.setReducerClass(WordSumReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}