import os
import re
import subprocess as subp

BORNE_MAX = 20000
MACHINES_CMD = "cat $OAR_FILE_NODES | uniq"
NB_MACHINES_CMD = "cat $OAR_FILE_NODES | uniq | wc -l"

SCRIPT_PI = "pi.py"

if __name__ == "__main__":
	machines = os.popen(MACHINES_CMD).read()
	nb_machines = int(str(os.popen(NB_MACHINES_CMD).read()))

	machines = re.split("\n", machines)

	intervalle = BORNE_MAX / nb_machines

	borne_min = 0
	borne_max = intervalle

	p = [None]*(nb_machines+1)

	i = 0
	for m in machines[:-1]:
 		p[i] = subp.Popen(["oarsh", m, "python " +  SCRIPT_PI + " "+ str(i) +" " +  str(borne_min) + " " + str(borne_max)])
		i+=1
		borne_min=borne_max
		borne_max=borne_max+intervalle
	
	for i in range(0, nb_machines):
		p[i].wait()

	files = [f for f in os.listdir('/home/afrass/Bureau/ppd/') if re.match(r'slice_[0-9]*', f)]
	result = 0
	for f in files:
		print f
		slicefile = open(f)
		result += eval(slicefile.read())
		slicefile.close()

	resultf = open("result.txt", 'w')
	resultf.write(str(result * 4))
	resultf.close()
