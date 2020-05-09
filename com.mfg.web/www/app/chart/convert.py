import csv
import json


data = []

with open('sample.csv', newline='') as csvfile:
	spamreader = csv.reader(csvfile, delimiter=',', quotechar='"')
	for row in spamreader:
		try:
			data.append({
				't':int(row[0]),
				'p':abs(int(row[1]))
			})
		except:
			# first row are names
			pass

			
with open('sample.js', 'w') as f:
	f.write('window.sampleData=')
	json.dump(data, f, indent=1)
	f.write(';');
		
