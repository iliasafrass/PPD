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

public class GrapheSocial {
	public static int tuples = 2; // >=2

	/* permet de mapper les donnees en entrees */
	/* mapper <entree, entree, sortie, sortie> */
	public static class EtendreMapper extends Mapper<Object, Text, Text, Text> {

		private String tmpClef = "";
		private String tmpValeur = "";

		/*
		 * But du jeu créé des tuples a tuples entrée. Calcul non fonctionnel
		 * quand une personne a moins d'amis que tuples
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
		private String res = "";
		private int nbDeConnaissanceEnCommun;

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			res = "";

			ArrayList<String> commun = new ArrayList<String>();
			ArrayList<String> tmpSplit = new ArrayList<String>();
			// construction des ensembles
			Iterator<Text> ite = values.iterator();
			String tmp[] = new String[tuples];
			int j = 0;
			while (ite.hasNext()) {
				tmp[j] = ite.next().toString();
				j++;
			}

			// creation d'un commum
			for (String s : tmp[0].split(" ")) {
				commun.add(s);
			}
			// TODO this.nbDeConnaissanceEnCommun = 0;

			// on supprime du commun
			for (int i = 1; i < tmp.length; i++) {
				// copy pour acces concurent
				@SuppressWarnings("unchecked")
				ArrayList<String> copyCommun = (ArrayList<String>) commun
						.clone();

				for (String s : tmp[i].split(" ")) {
					tmpSplit.add(s);
				}

				for (String str : tmpSplit) {
					if (!commun.contains(str)) {
						commun.remove(str);
					}
				}
				for (String str : copyCommun) {
					if (!tmpSplit.contains(str)) {
						commun.remove(str);
					}
				}
			}

			String communString = "";

			for (String s : commun) {
				// this.nbDeConnaissanceEnCommun++;
				this.nbDeConnaissanceEnCommun = commun.size();
				communString += s + " ";
			}

			// res = "(" + this.nbDeConnaissanceEnCommun + ") " + communString;

			context.write(key, new Text(communString));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "GrapheSocial");
		/* le driver */
		job.setJarByClass(GrapheSocial.class);

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