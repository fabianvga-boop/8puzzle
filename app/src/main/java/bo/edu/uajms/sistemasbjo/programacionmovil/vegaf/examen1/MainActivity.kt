package bo.edu.uajms.sistemasbjo.programacionmovil.vegaf.examen1

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val board = IntArray(16) { 0 }
    private val solvedBoard = IntArray(16) { i -> if (i < 15) i + 1 else 0 }

    private val buttons = Array<Button?>(16) { null }

    private lateinit var btnRestart: Button
    private lateinit var btnShuffle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        buttons[0] = findViewById(R.id.btn00)
        buttons[1] = findViewById(R.id.btn01)
        buttons[2] = findViewById(R.id.btn02)
        buttons[3] = findViewById(R.id.btn03)
        buttons[4] = findViewById(R.id.btn10)
        buttons[5] = findViewById(R.id.btn11)
        buttons[6] = findViewById(R.id.btn12)
        buttons[7] = findViewById(R.id.btn13)
        buttons[8] = findViewById(R.id.btn20)
        buttons[9] = findViewById(R.id.btn21)
        buttons[10] = findViewById(R.id.btn22)
        buttons[11] = findViewById(R.id.btn23)
        buttons[12] = findViewById(R.id.btn30)
        buttons[13] = findViewById(R.id.btn31)
        buttons[14] = findViewById(R.id.btn32)
        buttons[15] = findViewById(R.id.btn33)

        btnRestart = findViewById(R.id.btnRestart)
        btnShuffle = findViewById(R.id.btnMess)

        for (i in 0 until 16) {
            buttons[i]?.tag = i
            buttons[i]?.setOnClickListener { v -> onTileClicked(v) }
        }

        btnRestart.setOnClickListener { restartToInitial() }
        btnShuffle.setOnClickListener { shuffleAndStart() }

        initSolvedBoard()
        updateUI()
    }

    //FUNCION QUE INICIA EL TABLERO RESUELTO
    private fun initSolvedBoard() {
        for (i in 0 until 16) board[i] = solvedBoard[i]
        setBoardEnabled(true)
    }

    //FUNCION REINICIAR TABLERO INICIAL
    private fun restartToInitial() {
        initSolvedBoard()
        updateUI()
    }

    //FUNCION DESORDENAR Y INICIAR EL JUEGO
    private fun shuffleAndStart() {
        val shuffled = generateSolvableShuffle()
        for (i in 0 until 16) board[i] = shuffled[i]
        setBoardEnabled(true)
        updateUI()
    }

    private fun onTileClicked(view: View) {
        val index = (view.tag as? Int) ?: return
        val emptyIndex = board.indexOf(0)

        if (board[index] == 0) return

        if (isAdjacent(index, emptyIndex)) {
            val temp = board[index]
            board[index] = board[emptyIndex]
            board[emptyIndex] = temp
            updateUI()

            if (isSolved()) {
                setBoardEnabled(false)
                showWinDialog()
            }
        }
    }


    //FUNCION ACTUALIZAR TABLERO
    private fun updateUI() {
        for (i in 0 until 16) {
            val btn = buttons[i] ?: continue
            val value = board[i]
            if (value == 0) {
                btn.text = ""
                btn.isEnabled = false
                btn.alpha = 0.3f
            } else {
                btn.text = value.toString()
                btn.isEnabled = true
                btn.alpha = 1.0f
            }
        }
    }

    //FUNCION HABILITAR TABLERO
    private fun setBoardEnabled(enabled: Boolean) {
        for (i in 0 until 16) {
            val btn = buttons[i] ?: continue
            val value = board[i]
            btn.isEnabled = enabled && value != 0
        }
    }

    //FUNCION VERIFICAR ADYACENTE
    private fun isAdjacent(indexA: Int, indexB: Int): Boolean {
        val rowA = indexA / 4
        val colA = indexA % 4
        val rowB = indexB / 4
        val colB = indexB % 4

        val rowDiff = abs(rowA - rowB)
        val colDiff = abs(colA - colB)
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)
    }

    //FUNCION VERIFICAR SOLUCION
    private fun isSolved(): Boolean {
        for (i in 0 until 16) {
            if (board[i] != solvedBoard[i]) return false
        }
        return true
    }


    private fun generateSolvableShuffle(): IntArray {
        val list = IntArray(16)
        val rnd = Random(System.currentTimeMillis())
        do {
            val temp = (0..15).toMutableList()
            // Fisher-Yates shuffle
            for (i in temp.size - 1 downTo 1) {
                val j = rnd.nextInt(i + 1)
                val t = temp[i]
                temp[i] = temp[j]
                temp[j] = t
            }
            for (i in 0 until 16) list[i] = temp[i]

            val inversions = countInversions(list)
            val rowFromBottom = 4 - (list.indexOf(0) / 4) // 1..4
            val solvable = (inversions + rowFromBottom) % 2 == 1 //
            if (solvable && !list.contentEquals(solvedBoard)) {
                return list
            }
        } while (true)
    }

    private fun countInversions(arr: IntArray): Int {
        val flat = arr.filter { it != 0 }
        var inv = 0
        for (i in 0 until flat.size) {
            for (j in i + 1 until flat.size) {
                if (flat[i] > flat[j]) inv++
            }
        }
        return inv
    }

    //FUNCION MOSTRAR MENSAJE
    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle("¡Felicidades!")
            .setMessage("Has resuelto el puzzle.")
            .setPositiveButton("Reiniciar") { dialog, _ ->
                restartToInitial()
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
}



