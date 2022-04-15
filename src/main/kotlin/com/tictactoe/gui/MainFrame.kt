package com.tictactoe.gui

import com.tictactoe.Game
import com.tictactoe.Node
import com.tictactoe.load
import java.awt.BorderLayout
import java.awt.Component
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.border.EmptyBorder

//
//fun main() {
//    val frame: JFrame = MainFrame()
//
//    frame.isVisible = true
//}
class MainFrame: JFrame() {
    init {
        val game=Game()
        load(game.node)
    }

    fun load(node: Node) {
        //removeAll()
        layout = GridLayout(1, 3)
        add(MyList(false, node.parent.toList(), this))
        add(MyNode(true, node, this))
        add(MyList(false, node.child.toList(), this))
        //pack()
    }

    class MyList(readonly: Boolean, list: List<Node>, mainFrame: MainFrame) : JPanel() {
        init {
            layout = GridLayout(list.size, 1)
            list.forEach {
                add(MyNode(readonly, it, mainFrame))
            }
        }
    }

    class MyNode(readonly: Boolean, node: Node, mainFrame: MainFrame): JPanel() {
        val button = JButton("<html><table>" +
            "<tr>" +
            "${node.str.substring(0..2).map { "<td>$it</td>" }.joinToString("")}" +
            "</tr>" +
            "<tr>" +
            "${node.str.substring(3..5).map { "<td>$it</td>" }.joinToString("") }" +
            "</tr>" +
            "<tr>" +
            "${node.str.substring(6..8).map { "<td>$it</td>" }.joinToString("")}" +
            "</tr>" +
            "</table></html>")
        init {
            layout = BorderLayout()
            border = EmptyBorder(2,2,2,2)
            isEnabled = !readonly
            add(BorderLayout.CENTER, button)
            add(BorderLayout.SOUTH, JLabel("${node.winner} ${node.xWinningPossibilities.size-node.oWiningPossibilities.size} ${node.oLoseWithSteps}"))
            button.addActionListener(NodeAction(node, mainFrame ) )
//            node.str.forEach {
//                if(readonly) {
//                    add(JLabel(" $it "))
//                }else {
//                    val button = JButton(" $it ")
//                    button.addActionListener(NodeAction(node, mainFrame ) )
//                    add(button)
//                }
//            }
        }
    }

    class NodeAction(private val node: Node, private val mainFrame: MainFrame) : ActionListener {
        override fun actionPerformed(e: ActionEvent?) {
            SwingUtilities.invokeLater {
                mainFrame.contentPane.removeAll()
                mainFrame.load(node)
                mainFrame.pack()
            }

        }

    }

    class NodeRenderer(private val readonly:Boolean, private val mainFrame: MainFrame) : JPanel(), ListCellRenderer<Node> {
        init {

        }

        override fun getListCellRendererComponent(
            list: JList<out Node>,
            node: Node, index: Int, isSelected: Boolean, cellHasFocus: Boolean
        ): Component {

            return this
        }
    }
}

