import os
import sys

if __name__ == "__main__":
	task = sys.argv[1]
	start = int(sys.argv[2])
	end = int(sys.argv[3])
	res = 0
	for k in range(start, end):
		res += (pow((-1), k))/((2 * k + 1)+0.0)
	f = open("/home/afrass/Bureau/ppd/slice_" + task , 'w')
	f.write(str(res))
	f.close()