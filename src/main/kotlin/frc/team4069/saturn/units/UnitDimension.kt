package frc.team4069.saturn.units

sealed class UnitDimension

class Pure<N : Num> : UnitDimension()
class Neg<N : Num> : UnitDimension()
class Add<LHS : UnitDimension, RHS : UnitDimension> : UnitDimension()
class Sub<LHS : UnitDimension, RHS : UnitDimension> : UnitDimension()
class Mul<LHS : UnitDimension, RHS : UnitDimension> : UnitDimension()
class Div<LHS : UnitDimension, RHS : UnitDimension> : UnitDimension()


class SIUnit<Kg : UnitDimension, M : UnitDimension, S : UnitDimension, A : UnitDimension>(val value: Double) {
    operator fun plus(other: SIUnit<Kg, M, S, A>): SIUnit<Kg, M, S, A> =
        SIUnit(value + other.value)

    operator fun <Kg2 : UnitDimension, M2 : UnitDimension, S2 : UnitDimension, A2 : UnitDimension> times(other: SIUnit<Kg2, M2, S2, A2>)
            : SIUnit<Add<Kg, Kg2>, Add<M, M2>, Add<S, S2>, Add<A, A2>> =
        SIUnit(value * other.value)

    operator fun <Kg2 : UnitDimension, M2 : UnitDimension, S2 : UnitDimension, A2 : UnitDimension> div(other: SIUnit<Kg2, M2, S2, A2>)
            : SIUnit<Sub<Kg, Kg2>, Sub<M, M2>, Sub<S, S2>, Sub<A, A2>> =
        SIUnit(value / other.value)
}


val Number.coulomb get() = SIUnit<Pure<N0>, Pure<N0>, Pure<N1>, Pure<N1>>(toDouble())
val Number.amp get() = SIUnit<Pure<N0>, Pure<N0>, Pure<N0>, Pure<N1>>(toDouble())
val Number.second get() = SIUnit<Pure<N0>, Pure<N0>, Pure<N1>, Pure<N0>>(toDouble())
val Number.meter get() = SIUnit<Pure<N0>, Pure<N1>, Pure<N0>, Pure<N0>>(toDouble())
val <Kg: UnitDimension, M: UnitDimension, A: UnitDimension> SIUnit<Kg, M, Pure<N0>, A>.velocity get() = SIUnit<Kg, M, Neg<N1>, A>(value)
