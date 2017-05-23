import sys


slice = "slice_"

if __name__ == "__main__":
	nb_slice = int(sys.argv[1])
	result = 0
	files = [f for f in os.listdir('.') if re.match(r'[0-9]+.*\.jpg', f)]
	for f in files:
		file = open(f)
		result += eval(file.read())
		file.close()
	print(result * 4)