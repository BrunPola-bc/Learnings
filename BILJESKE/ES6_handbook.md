Ovo je prijepis članka https://dev.to/shreya/es6-handbook-everything-you-need-to-know-1ea7 generiran pomoću ChatGPT-a, za slučaj da se online verzija iz bilo kojeg razloga ukloni.

# ES6 Handbook: Key Concepts & Examples

## Table of Contents
- [let / const](#let--const)
- [Objects](#objects)
- [this](#this)
- [Arrow Functions](#arrow-functions)
- [Destructuring](#destructuring)
- [Spread Operator](#spread-operator)
- [Classes](#classes)
- [Inheritance](#inheritance)
- [Modules](#modules)
- [Template Literals](#template-literals)
- [Promises](#promises)
- [Rest Parameters & for..of](#rest-parameters--forof)

---

## let / const

### var
```js
var name = 'Jack'; // global scope
function message() {
    var msg = 'Hey Jack!'; // function scope
}
console.log(msg); // ERROR
```

### let
- Block scoped; cannot redeclare in same scope
```js
let x = 1;
// let x = 3; // ❌ SyntaxError
```

### const
- Block scoped and immutable
```js
const m = 8;
m = 5; // ❌ TypeError
```

---

## Objects
```js
const car = {
    model: 'Tesla',
    color: 'black',
    price: 800
};
```
- Shorthand when key = variable name:
```js
const model = 'Tesla';
const car = { model };
```

---

## this
- Refers to the object inside methods
- Standalone function `this` is undefined in strict mode

---

## Arrow Functions
- Shorter syntax, lexical `this`
```js
const square = num => num * num;
const items = colors.map(color => `<li>${color}</li>`);
```

---

## Destructuring
```js
const { name, age } = person;
const { country: ctry } = person; // alias
```

---

## Spread Operator
### Arrays
```js
const combined = [...arr1, ...arr2];
```
### Objects
```js
const merged = {...obj1, ...obj2};
```

---

## Classes
```js
class Boy {
    constructor(name) { this.name = name; }
    run() { console.log("running..."); }
}
const boy = new Boy("Sam");
```

## Inheritance
```js
class Girl extends Boy {
    constructor(name, age) {
        super(name);
        this.age = age;
    }
}
```

---

## Modules
### boy.js
```js
export class Boy { ... }
```
### girl.js
```js
import { Boy } from './boy';
export class Girl extends Boy { ... }
```
- Named vs Default exports:
```js
export default class Car { ... }
```

---

## Template Literals
```js
const greeting = `Hello ${name}`;
```

## Promises
```js
fetch(url)
  .then(res => res.json())
```

## Rest Parameters & for..of
```js
function foo(...args) { console.log(args); }
for (const x of array) { console.log(x); }
```
