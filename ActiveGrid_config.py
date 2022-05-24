import numpy as np

################################################
#defines which buttons are in which sets
# ADD YOUR SETS
################################################

set_array = [ 
[1], # set 1 contains button 1
[2,9], # set 2 contains buttons 2,9
[3,10,17],  # set 3 contains buttons 3,10,17 
[4,11,18,25],  # etc
[5,12,19,26,33], 
[6,13,20,27,34,41], 
[7,14,21,28,35,42,49], 
[8,15,22,29,36,43,50,57], 
[16,23,30,37,44,51,58], 
[24,31,38,45,52,59], 
[32,39,46,53,60], 
[40,47,54,61], 
[48,55,62], 
[56,63], 
[64] # 15 sets total
]

################################################


####################################################################
#you need a timestamp array FOR EVERY SET YOU DEFINED ABOVE
#you also need an angle array FOR EVERY SET YOU DEFINED ABOVE
# DEFINE YOUR ANGLE AND TIMESTAMP TABLES HERE
####################################################################


timestamp_array = [] #should eventually contain 15 other arrays corrresponding to each set
angle_array = [] # same here

#generate a list of times, specifying the timestep and experiement length in ms
#only positive numbers and integers allowed for times
# integer angles from -90 to 90

experiment_length = 2000
timestep = 1000
timestamp = np.arange(0,experiment_length+1,timestep)
print(timestamp)
timestamp = timestamp.astype(int)
angle = np.linspace(-90,90,len(timestamp));
angle = angle.astype(int)

for i in range(0,len(set_array)):
    timestamp_array.append(timestamp + (200*i));
    angle_array.append(angle);



#SPECIFY A FILENAME HERE
filename = 'test_python.txt'

#####################################################################






################################################################
# CREATES THE CONFIGURATION FILE
# ONLY RUN THIS AFTER RUNNING THE ABOVE SECTION
# ITERATES OVER ALL WINGLETS AND MOVEMENT ARRAYS
# DO NOT CHANGE THIS SECTION
################################################################

fid = open(filename,'w');

in_str = "Buttons"+"\n";
fid.write(in_str);
for i in range(0,len(set_array)):
    
    set = set_array[i];
    for j in range(0,len(set)):
        button_str = str(set[j])+":"+str(i+1)+"\n"
        fid.write(button_str)
        


mid_str = "End Buttons"+"\n"
fid.write(mid_str)


for i in range(0,len(timestamp_array)):

    fid.write("Movements " + str(i+1) + "\n");
    time_str = "";
    move_str = "";
    timestamp = timestamp_array[i];
    angle = angle_array[i];
    for j in range(0,len(timestamp)):
        time_str = time_str+str(timestamp[j])+","
        move_str = move_str+str(angle[j])+","
    print(time_str)
        

    time_str = time_str+"\n"
    move_str = move_str+"\n";
    fid.write(time_str)
    fid.write(move_str)


out_str = "End Movements"+"\n"
fid.write(out_str)
fid.close();

#################################################################3