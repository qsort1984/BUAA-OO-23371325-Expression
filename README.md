# 符号表达式解析与化简程序

一个简单的符号表达式解析与化简程序：支持对包含多项式、三角函数、函数调用与求导操作的表达式进行解析，转为内部的“多项式-单项式”结构，随后完成合并同类项与部分恒等变换化简，输出标准化字符串结果。

## 支持的表达式能力

- 基本运算：`+`、`-`、`*`、括号 `()`、幂 `^`
- 变量：`x`（支持 `x^k`，其中 `k` 为非负整数）
- 常数：任意长度整数（使用 `BigInteger` 保存系数）
- 三角函数：`sin(f)`、`cos(f)`，并支持 `sin(f)^k`、`cos(f)^k`
- 求导：`dx(expr)`，对内部表达式对 `x` 求导
- 自定义函数调用：
  - `f{d}(a)` / `f{d}(a,b)`：递推/显式定义的函数族（`d` 为一位数字，内部用数组保存 0-9）
  - `g(a)` / `g(a,b)`、`h(a)` / `h(a,b)`：通过输入定义的函数

表达式会在读入后先去除空格与制表符再进行词法分析与语法分析。

## 输入输出格式

程序入口为 [MainClass.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/MainClass.java#L1-L31)，标准输入格式为：

1. 第一行：整数 `m`，表示接下来有 `m` 行 `g/h` 的函数定义
2. 接下来 `m` 行：每行一条 `g(...) = ...` 或 `h(...) = ...` 定义（顺序不限，读取后分别保存）
3. 下一行：整数 `n`
4. 接下来 `3*n` 行：`f{...}(...)=...` 的定义行（包含若干条显式 `f{0}`/`f{1}`/... 或一条形如 `f{n}(...)=...` 的递推定义）
   - 当 `n == 1` 时，会基于递推定义推导出 `f{2}..f{5}`（见 [FuncDef.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/FuncDef.java#L103-L117)）
5. 最后一行：需要计算/化简的表达式

输出：化简后的表达式字符串（见 [Polynomial.toString](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Polynomial.java#L259-L290) 与 [Monomial.toString](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Monomial.java#L396-L443)）。

## 语法结构（对应 Parser 实现）

核心递归下降解析见 [Parser.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Parser.java#L1-L255)：

- `expr`：若干 `term` 用 `+/-` 连接
- `term`：若干 `factor` 用 `*` 连接（并带有项级别的正负号）
- `factor` 支持：
  - 数字（可带一元 `+/-`）
  - `x` 或 `x^k`
  - `(expr)` 或 `(expr)^k`
  - `sin(factor)` / `sin(factor)^k`
  - `cos(factor)` / `cos(factor)^k`
  - `dx(expr)`
  - `f{d}(factor)` / `f{d}(factor,factor)`
  - `g(factor)` / `g(factor,factor)`，`h(factor)` / `h(factor,factor)`

## 化简规则（实现层面）

内部表示为 `Polynomial = Set<Monomial>`：

- 单项式 `Monomial`：`coefficient * x^exponent * ∏sin(P)^a * ∏cos(P)^b`
  - 其中 `P` 也是一个 `Polynomial`，因此 `sin/cos` 的自变量支持表达式嵌套（见 [Monomial.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Monomial.java)）
- 合并同类项：`Polynomial.addMono` 基于“除系数外完全相同”的 `Monomial.isSimilar` 合并系数（见 [Polynomial.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Polynomial.java#L57-L75)）
- 三角函数与符号化简：
  - `sin(-P)` 与 `cos(-P)` 的合并（调整符号/指数），见 [Monomial.assistSimplify](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Monomial.java#L124-L162)
  - 利用 `sin(2a)=2*sin(a)*cos(a)` 的逆向折叠：当系数含有足够的 `2` 因子且同一自变量的 `sin(a)^k` 与 `cos(a)^k` 同时出现时，折叠为 `sin(2a)^k`（见 [Monomial.simplify](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Monomial.java#L180-L201) 与 [Polynomial.mul(int)](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Polynomial.java#L110-L120)）
  - 利用 `sin^2(a)+cos^2(a)=1` 的合并：在 `Polynomial.merge` 中尝试对满足条件的两项做指数削减（见 [Polynomial.merge](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Polynomial.java#L161-L192) 与 [Monomial.canMerge](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Monomial.java#L329-L359)）

最终输出前会执行 `Polynomial.simplify()`，包括对子项 `Monomial.simplify()`、再次合并同类项、以及一次 `merge()` 过程（见 [Polynomial.simplify](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Polynomial.java#L228-L247)）。

## 求导规则

求导入口为 `dx(expr)`，对应 [DeriveFactor.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/DeriveFactor.java)。

导数实现见 [Derive.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/Derive.java)：

- 多项式：逐单项式求导并相加
- 单项式：
  - `x^n` 部分：`n*x^(n-1)`（`n=0` 时自然为 0）
  - `sin(P)^t`：`t*sin(P)^(t-1)*cos(P)*P'`
  - `cos(P)^t`：`-t*cos(P)^(t-1)*sin(P)*P'`
  - 对同一个单项式中每个 `sin/cos` 因子分别求导并累加（相当于乘积求导展开）

## 自定义函数（f/g/h）机制

- `g/h` 定义：通过输入行解析并保存为 `Func`，见 [GhDef.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/GhDef.java)
- `f{d}` 定义：显式定义或递推定义解析见 [FuncDef.java](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/FuncDef.java)
- 调用展开：调用时将实参表达式替换进定义字符串，再用 `Lexer+Parser` 重新解析得到多项式（见 [FuncFactor.getPoly](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/FuncFactor.java#L17-L35) 与 [GhFactor.getPoly](file:///c:/Users/19395/IdeaProjects/oo_homework_2025_23371325_hw_3/src/GhFactor.java#L17-L35)）

## 运行方式（命令行示例）

本项目无 `package` 声明，直接编译运行即可（PowerShell 示例）：

```powershell
cd c:\Users\19395\IdeaProjects\oo_homework_2025_23371325_hw_3
javac -encoding UTF-8 -d out\classes src\*.java
java -cp out\classes MainClass
```

程序从标准输入读取数据，向标准输出打印化简后的结果。
