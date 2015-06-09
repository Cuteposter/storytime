#Version=0.01

#NOTES
#Total number of flags must be set

#Story nodes must be named first, and a start node must be defined
#Ending nodes have no children

#Story node format goes like this:
#name(scene, {charL, charC, charR}, speaker, emote, sound, "dialog", {options}, {flags_to_set}, {flags_to_branch}, {children})
#As of this version, commas must be followed by spaces
#all items wrapped in curly braces are comma separated lists with no spaces

#Still working on including outside content, for now, use built in engine shit
#Values for resource-bound parameters below

#VALUES
#scene: Gamestop.jpg
#emote: question
#sound: surprise.wav, wonder.wav, yay.wav
#characters: 2ds, vita
#speaker: left, right, or center. !!!MUST BE A NON-NULL CHARACTER!!!
#flags_to_set: any flag 0 to the maximum you define. -1 is no flag.

#PARAMETERS
#If you want to skip a parameter, use null
#For the character paramter: you can also use null to skip a character, but at least one must be set
#Option paramters must be strings (enclosed in quotation marks)
#If using flags_to_branch, the last node of children is the fall through. That is, if nothing is true, the game will branch there.

flags=5
nodes=start, N1B1, N1B2, N1B3, N2BA, N3BA, NE, NE2, NE3

start(Gamestop.jpg, {null,2ds,null}, center, null, null, "I'm pretty [C:blue]hungry today. Let's make a [C:green]sandwich! \nDo you want to help me make a [C:green]sandwich?", {"Yes","No","MOOOOOOOODS"}, {0,-1,-1}, null, {N1B1,N1B2,N1B3})
N1B1(Gamestop.jpg, {2ds,null,vita}, right, question, wonder.wav, "What should the [C:green]bread be?", {"White","Wheat"}, {1,2}, null, {N2BA})
N1B2(Gamestop.jpg, {2ds,null,null}, left, null, null, "HOLY SHIT I FUCKING HATE YOU [C:green]/v/", null, null, null, null)
N1B3(Gamestop.jpg, {2ds,null,null}, left, null, null, "[C:red]AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, null, null, null)
N2BA(Gamestop.jpg, {2ds,null,vita}, left, question, wonder.wav, "What should the [C:blue]meat be?", {"Ham","Turkey"}, {3,4}, null, {N3BA})
N3BA(Gamestop.jpg, {null,vita,null}, center, null, yay.wav, "Yummy! A [C:green][F:3:ham,turkey] on [C:blue][F:1:white,wheat] sandwich!", null, null, {"1,3","2,4"}, {NE,NE2,NE3})
NE(Gamestop.jpg, {null,vita,null}, center, null, null, "Thanks for helping us make a tasty [C:green]sandwich. We really liked it!", null, null, null, null)
NE2(Gamestop.jpg, {null,2ds,null}, center, null, null, "Thanks for helping us make a tasty [C:green]sandwich. We really liked it!", null, null, null, null)
NE3(Gamestop.jpg, {2ds,null,vita}, left, null, null, "Thanks for helping us make a tasty [C:green]sandwich. We really liked it!", null, null, null, null)
