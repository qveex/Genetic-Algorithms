import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm

def f(x, y):
    return -np.cos(x) * np.cos(y) * np.exp(-((x - np.pi)**2 + (y - np.pi)**2))

x = np.linspace(1, 5, 25)
y = np.linspace(1, 5, 25)

X, Y = np.meshgrid(x, y)
Z = f(X, Y)

xdata = []
ydata = []
zdata = []

with open('result.txt') as f:
    lines = f.readlines()
    x = []
    y = []
    z = []
    for line in lines:
        if line == "\n":
            xdata.append(x)
            ydata.append(y)
            zdata.append(z)
            x = []
            y = []
            z = []
            continue
        line = line.split('\t')
        point = [i.strip().replace(',', '.') for i in line]
        
        x.append(float(point[0]))
        y.append(float(point[1]))
        z.append(float(point[2]))

fig = plt.figure()

titles = ['Init population', 'Last population']
for i in range(0,2):
    ax = fig.add_subplot(1, 2, i+1, projection='3d')
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    ax.set_zlabel('z')
    ax.plot_surface(X, Y, Z, cmap='coolwarm', alpha=0.5)
    ax.scatter3D(xdata[i], ydata[i], zdata[i], s=50, c='#ff0000')
    ax.set_title(titles[i])
    ax.set_xlim(1, 5)
    ax.set_ylim(1, 5)
    ax.set_zlim(-1.5, 0.5)

plt.show()