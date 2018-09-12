let addx x = x + 1

let add2 x =
  let x1 = addx x in
  let x2 = addx x1 in
  x2

let addx x = x + x

let _ = Printf.printf "%d\n" (add2 2)
