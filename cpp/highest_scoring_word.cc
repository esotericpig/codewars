#include <iostream>
#include <regex>
#include <string>

/**
 * @author Jonathan Bradley Whited
 * @see    https://www.codewars.com/kata/highest-scoring-word/cpp
 * @rank   6 kyu
 */
std::string highestScoringWord(const std::string& str) {
  std::regex re("[^\\s]+");
  auto it_end = std::sregex_iterator();

  std::string best;
  long bestScore = 0L;

  for(auto it = std::sregex_iterator(str.begin(),str.end(),re); it != it_end; ++it) {
    std::string s = it->str();
    long score = 0L;

    for(auto& c: s) {
      score += (c - 'a' + 1);
    }

    if(best.empty() || score > bestScore) {
      best = s;
      bestScore = score;
    }
  }

  return best;
}

int main(int argc,char** argv) {
  std::cout << highestScoringWord("man i need a taxi up to ubud") << std::endl;             // "taxi"
  std::cout << highestScoringWord("what time are we climbing up the volcano") << std::endl; // "volcano"
  std::cout << highestScoringWord("take me to semynak") << std::endl;                       // "semynak"
  std::cout << highestScoringWord("massage yes massage yes massage") << std::endl;          // "massage"
  std::cout << highestScoringWord("take two bintang and a dance please") << std::endl;      // "bintang"

  if(argc > 1) {
    std::string s;

    for(int i = 1; i < argc; ++i) {
      s += argv[i];
      s += ' ';
    }

    std::cout << std::endl << s << "=> " << highestScoringWord(s) << std::endl;
  }

  return 0;
}
