
import random

def rand(num, startTs, endTs):
  ret = []
  for i in range(num):
    t = random.randint(startTs, endTs)
    ret.append(t)
  return ret

num = 50
ts = rand(num, 1514888620000 - 86400, 1514888620000)
ts.sort()
vs = rand(num, 30, 3301)

print(ts)
print(vs)

# for i in range(len(ts)):
#   print "%d:%d," % (ts[i], vs[i])