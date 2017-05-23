import os
import re
import sys
import subprocess as subp

BORNE_MAX = 20000
MACHINES_CMD = "cat $OAR_FILE_NODES | uniq | sort"
NB_MACHINES_CMD = "cat $OAR_FILE_NODES | uniq | wc -l"

SCRIPT_DEPLOY = "/home/afrass/Bureau/ppd/deploy_tree.py"
SCRIPT_PI = "/home/afrass/Bureau/ppd/pi.py"

def extract_result():
	files = [f for f in os.listdir('/home/afrass/Bureau/ppd/') if re.match(r'slice_[0-9]*', f)]
	result = 0
	for f in files:
		slicefile = open(f)
		result += eval(slicefile.read())
		slicefile.close()

	resultf = open("result.txt", 'w')
	resultf.write(str(result * 4))
	resultf.close()

if __name__ == "__main__":

	# Recuperation de la tache courante
	task = int(sys.argv[1])
	print "tache " + str(task) + " : est dans la place."

	# Recuperation de la liste des machines
	machines = None
	nb_machines = None
	if task == 0:
		machines = os.popen(MACHINES_CMD).read()
		machines = re.split("\n", machines)
		nb_machines = int(str(os.popen(NB_MACHINES_CMD).read()))
		machine_file = open("/home/afrass/Bureau/ppd/machines.txt", 'w')
		for m in machines:
			machine_file.write(m + "\n")
		machine_file.close()
	else:
		machine_file = open("/home/afrass/Bureau/ppd/machines.txt", 'r')
		data = machine_file.read()
		machines = re.split("\n", data)[:-2]
		nb_machines = len(machines)

	intervalle = int(BORNE_MAX / nb_machines)

	p = [None]*(3)
	# Determination des taches a lancer
	task1 = task * 2 + 1
	task2 = task1 + 1

	# Lancement des sous taches 1 et 2
	if task1 <= len(machines) - 1:
		borne_min = task1 * intervalle
		borne_max = borne_min + intervalle
		print "tache " + str(task) + " : deploy task " + str(task1) + "."
		p[1] = subp.Popen(["oarsh", machines[task1], "python " + SCRIPT_DEPLOY + " "+ str(task1)])

	if task2 <= len(machines) - 1:
		borne_min = task1 * intervalle
		borne_max = borne_min + intervalle
		print "tache " + str(task) + " : deploy task " + str(task2) + "."
		p[2] = subp.Popen(["oarsh", machines[task2], "python " + SCRIPT_DEPLOY + " "+ str(task2)])

	# Lancement de la tache local
	borne_min = task * intervalle
	borne_max = borne_min + intervalle
	print "tache " + str(task) + " : calcul pi."
	p[0] = subp.Popen(["oarsh", machines[task], "python " + SCRIPT_PI + " "+ str(task) + " " + str(borne_min) + " " + str(borne_max)])

	# Attente des taches locales et des sous tache
	for i in range(0, 3):
		if p[i] != None:
			p[i].wait()

	if task == 0:
		print "tache " + str(task) + " : extract result."
		extract_result()