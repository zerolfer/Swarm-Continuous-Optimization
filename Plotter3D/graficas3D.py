import numpy as np
from mpl_toolkits import mplot3d
import matplotlib.pyplot as plt


def ackley(x, y):
    return -(-20 * np.exp(-0.2 * np.sqrt(0.5 * (x ** 2 + y ** 2))) - np.exp(
        0.5 * (np.cos(2 * np.pi * x) + np.cos(2 * np.pi * y))) + np.e + 20)


def himmelblau(x, y):
    return -(x ** 2 + y - 11) ** 2 - (x + y ** 2 - 7) ** 2


def g(x, y):
    return himmelblau(x - 5, y - 5)


start, end, precission = 0, 15, 500
x = np.linspace(start, end, precission)
y = np.linspace(start, end, precission)
X, Y = np.meshgrid(x, y)
Z = g(X, Y)
fig = plt.figure()
ax = plt.axes(projection='3d')
ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap='viridis', edgecolor='none')

ax.set_xlabel('x')
ax.set_ylabel('y')
ax.set_zlabel('z')

plt.show()
# ---

plt.figure()
cp = plt.contourf(X, Y, Z)
plt.colorbar(cp)

plt.show()
