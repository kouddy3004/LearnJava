# Framework for Data Extrapolation
For Copying generated CSV file into Linux:
pscp "Path of the filename" username@linuxserver:"CopyPath"

sample 
pscp -P 22 D:\koushik\myGit\LearnJava\src\main\java\scripts\java\result\STG_INS_POLICY_CASH_FLOW\STG_INS_POLICY_CASH_FLOW.csv qauser17@whf00dej://scratch/qauser17/DataExtrapolAtion


# Framework for Rest assured
1)
	Fill the environment details  in \src\main\java\restAssured\dataBank\environment.csv
	And Pass that as a parameter in main
2)
	Your environment Name should be as same as json file Name(Note : It's a case sensitive)
	Follow The same
	