package src;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Element;

public class Trabalho {
	public static JFrame frame = new JFrame("Trabalho 4 - LP II");
	
	public static JMenuBar menuBar = new JMenuBar(); // barra de menu
	public static JMenu menu = new JMenu("Arquivo"); // menu 
	public static JTabbedPane tabs = new JTabbedPane(); // conjunto de tabs

	public static File current_file; // arquivo aberto
	public static String current_filename; // nome do arquivo aberto
	public static JTextArea current_content; // conteúdo do arquivo aberto
	public static JTextField current_info; // caminho/linha atual do arquivo aberto

	//main
	public static void main(String[] args) {
		/** Método para definir características iniciais do JFrame.
		 * */
		frame.setSize(800,1000); // tamanho inicial do JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // comportamento da opção de sair do JFrame
		setHeader(); // define os botões da barra de menu
		frame.setJMenuBar(menuBar); // adiciona a barra de menu ao JFrame
		frame.add(tabs); // adiciona o JTabbedPane ao JFrame
		frame.setVisible(true); // torna o JFrame visível
	}
	
	//addTab
	public static void addTab(JPanel panel, String text) {
		/** Adiciona um componente JPanel a uma nova tab dentro do JTabbedPane. */
		tabs.add(text,panel);		
	}
	
	//openFile
	public static void openFile() {
		/** Abre um arquivo e atribui seu conteúdo a um componente JTextArea. Também é salvo e mostrado
		 * o caminho atual do arquivo e, se houver, a linha atual do conteúdo que o usuário está editando.
		 * */
		
		// remoção da tab anterior a tab que será inserida no final deste método
		int index = tabs.getComponents().length;
		if(index>=1) {
			tabs.remove(0);
		}
			
		
		// abre arquivo
		JFileChooser file_chooser = new JFileChooser();
	    FileNameExtensionFilter file_filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
	    file_chooser.setFileFilter(file_filter);
	    
	    int return_value = file_chooser.showOpenDialog(new JTextArea(1,10));
	    if(return_value == JFileChooser.APPROVE_OPTION) {
	    	try {
				current_file = file_chooser.getSelectedFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    Path p = Paths.get(current_file.getPath());
	    String file_name = p.getFileName().toString();
	    
	    current_filename = file_name.replace(".txt","");
	    
	    // get file content
	    try {
	    	JPanel base_panel = new JPanel();
	    	base_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    	
			Scanner file_scanner = new Scanner(new FileInputStream(current_file));
			String buffer = "";
			
			while(file_scanner.hasNext()) {
				buffer += file_scanner.nextLine() + "\n";
			}
						
			JTextArea textArea = new JTextArea(buffer,50,50);
			textArea.setEditable(true);
			textArea.addCaretListener(new CaretListener() {
		            @Override
		            public void caretUpdate(CaretEvent e) {
		            	try {
		            		int caretPosition = textArea.getCaretPosition();
		        		    Element root = textArea.getDocument().getDefaultRootElement();
		        		    int current_line = root.getElementIndex( caretPosition );
		        		    current_line++;	   
		        		    String current_path = "";
		        		    
		        		    if(current_file!=null) {
		        		    	current_path = current_file.getAbsolutePath();
		        		    }
		        		    
		        		    String label_text = "Caminho atual: "+ current_path + "     Linha atual: "+current_line;
		        		    updateInfo(label_text);
		        		    
		        		    if(current_info!=null) {
		        		    	current_info.addActionListener(new ActionListener() {
			        				 @Override
			        		         public void actionPerformed(ActionEvent e) {
			        					 updateInfo(label_text);
			        					 current_info.setVisible(true);
			        		         }
			        		    });
		        		    }

		            	} catch (Exception e1) {
							e1.printStackTrace();
						}
		            	
		            }
		        });
			
			current_content = textArea;
			
			JScrollPane scrollPane = new JScrollPane(textArea); 
			scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );    
			scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );  
			scrollPane.setBounds(50, 50, 50, 50);
			
		    // set file content on new tab
		    base_panel.add(scrollPane);
		    
		    current_info = new JTextField();
		    current_info.setColumns(50);
		    
		    if(current_file!=null) {
		    	String current_path = current_file.getAbsolutePath();
		    	String label_text = "Caminho atual: "+ current_path;
			    updateInfo(label_text);
		    }
		    
		    current_info.setVisible(true);
		    base_panel.add(current_info, JPanel.BOTTOM_ALIGNMENT);
		    
		    // add new tab on tabpane
		    addTab(base_panel, current_filename);
			frame.setVisible(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//getCurrentContent
	public static String getCurrentContent() {
		/** Retorna o texto do conteúdo atual, tal conteúdo é armazenado em um componente JTextField.
		 * @return: texto atual.
		 * */
		if(current_content!=null && current_file!=null) return current_content.getText();
		else return "";
	}
	
	//saveFileContent
	public static void saveFileContent() {
		/** Opção de 'Salvar arquivo' do menu. Este método salva o conteúdo disponível atual no arquivo aberto.
		 * */
		if(current_content!=null && current_file!=null) {
			try {
				PrintWriter printWriter = new PrintWriter(current_file);
				printWriter.write(current_content.getText());
				printWriter.close();
		    	JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
	    	JOptionPane.showMessageDialog(null, "Sem arquivo aberto para salvar!");
	    }
	}
	
	//saveFileContentAs
	public static void saveFileContentAs() {
		/** Opção de 'Salvar arquivo como...' do menu. Este método cria um novo arquivo com o conteúdo disponível atual.
		 * */
		if(current_content!=null && current_file!=null) {
			// escolher um nome/diretório para o novo arquivo
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Escolha um arquivo para salvar");   
			 
			int userSelection = fileChooser.showSaveDialog(fileChooser);
			 
			if (userSelection == JFileChooser.APPROVE_OPTION) {
			    File fileToSave = fileChooser.getSelectedFile();
			    
			    try {
					// salvar o novo arquivo com o contéudo atual
			    	File file2;
			    	
			    	if(fileToSave.getName().contains(".txt")) {
					    file2 = new File(fileToSave.getPath());
			    	} else {
					    file2 = new File(fileToSave.getPath()+".txt");
			    	}

				    FileWriter writer2 = new FileWriter(file2);

				    String content = getCurrentContent();
				    if(!content.isEmpty()) {
						writer2.write(content);
					    writer2.close();
				    	JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!");
				    } 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
	    	JOptionPane.showMessageDialog(null, "Sem arquivo aberto para salvar!");
	    }
	}
	
	//closeFile
	public static void closeFile() {
		/** Os atributos referente ao último arquivo se tornam vazios. Sendo assim, não é permitido a edição
		 * do último arquivo aberto, apenas se o usuário abrir ele novamente. 
		 * */
		current_content.setEditable(false);		
		current_file = null;
		current_filename = null;
		current_info = null;
		
    	JOptionPane.showMessageDialog(null, "Este arquivo não pode ser mais editado. Para editá-lo, abra-o novamente!");
	}
	
	//updateInfo
	private static void updateInfo(String text) {
		/** Atualiza o texto presente no componente JTextField chamado current_info.
		 * @param text: texto para atualizar texto do JTextField. */
		if(current_info!=null) current_info.setText(text);
    }
	
	//setHeader
	public static void setHeader() {
		/** Adiciona botões ao menu do projeto.
		 * */
		
		// botões presentes no menu
		JMenuItem open = new JMenuItem("Abrir arquivo");
		JMenuItem save = new JMenuItem("Salvar arquivo");
		JMenuItem save_as = new JMenuItem("Salvar arquivo como...");
		JMenuItem close = new JMenuItem("Fechar arquivo");
		
		// ações dos botões presentes no menu
		open.addActionListener(new ActionListener() {
			 @Override
	         public void actionPerformed(ActionEvent e) {
				 openFile();
	         }
	    });
		
		save.addActionListener(new ActionListener() {
			 @Override
	         public void actionPerformed(ActionEvent e) {
				 saveFileContent();
	         }
	    });
		
		save_as.addActionListener(new ActionListener() {
			 @Override
	         public void actionPerformed(ActionEvent e) {
				 saveFileContentAs();
	         }
	    });
		
		close.addActionListener(new ActionListener() {
			 @Override
	         public void actionPerformed(ActionEvent e) {
				 closeFile();
	         }
	    });
		
		// adiciona os botões ao menu
		menu.add(open);
		menu.add(save);
		menu.add(save_as);
		menu.add(close);
		
		// adiciona o menu ao menu bar
		menuBar.add(menu);
	}
}
