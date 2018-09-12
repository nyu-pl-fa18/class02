function addx(x) { return x + 1; }

function add2(x) {
  x = addx(x);
  x = addx(x);
  return x;
}

function addx(x) { return x + x; } // Redeclaration of addx

console.log(add2(2))
