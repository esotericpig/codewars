#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>

/**
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/build-tower/cpp
 * @rank   6 kyu
 */
class Kata {
public:
  std::vector<std::string> towerBuilder(int nFloors) {
    int floorLen = (nFloors << 1) - 1;
    std::vector<std::string> tower(nFloors);

    for(int y = 0; y < nFloors; ++y) {
      std::string floor(floorLen,' ');
      int starLen = (y << 1) + 1;
      int spaceLen = (floorLen - starLen) >> 1;

      floor.replace(spaceLen,starLen,starLen,'*');
      tower[y] = floor;
    }

    return tower;
  }
};

std::ostream& operator<<(std::ostream& out,std::vector<std::string> tower) {
  for(std::string s: tower) {
    out << s << std::endl;
  }
  return out;
}

int main(int argc,char** argv) {
  Kata k;

  std::cout << k.towerBuilder(11);

  if(argc > 1) {
    for(int i = 1; i < argc; ++i) {
      int nFloors = std::atoi(argv[i]);

      std::cout << std::endl << nFloors << ':' << std::endl;
      std::cout << k.towerBuilder(nFloors);
    }
  }

  return 0;
}
