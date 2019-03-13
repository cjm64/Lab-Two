"""


class Person:
  def __init__(self, name, age):
    self.name = name
    self.age = age

p1 = Person("John", 36)

print(p1.name)
print(p1.age)

"""


class theWorld:
    boundaryX = None
    boundaryY = None


class thing:

    name = ""
    sizeX = None
    sizeY = None

    direction = None # 1 or -1

    # we can add like an image reference here too like: image = "anImage.png"

    def __init__(self, x, y):
        self.x = x
        self.y = y
