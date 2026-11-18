package org.aes.Ui;

import org.aes.Core.AES;
import org.aes.Models.CypherData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class MainWindow extends JFrame {

    private static final Color COLOR_BG = Color.WHITE;
    private static final Color COLOR_CARD_BG = new Color(212, 212, 212);
    private static final Color COLOR_CARD_BORDER = new Color(185, 185, 185);
    private static final Color COLOR_FIELD_BG = Color.WHITE;
    private static final Color COLOR_FIELD_BORDER = new Color(180, 180, 180);
    private static final Color COLOR_BTN_BG = Color.WHITE;
    private static final Color COLOR_BTN_BORDER = new Color(200, 200, 200);
    private static final Color COLOR_BTN_HOVER = new Color(245, 245, 245);
    private static final Color COLOR_TEXT_MAIN = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_MUTED = new Color(71, 85, 105);

    private static final int SECTION_LABEL_WIDTH = 135;

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    private JRadioButton rbEncrypt;
    private JRadioButton rbDecrypt;

    private JRadioButton rbECB;
    private JRadioButton rbCBC;
    private JPanel initializationVectorPanel;
    private JTextField txtIV;
    private JButton btnGenerateIV;

    private JRadioButton rbText;
    private JRadioButton rbFile;
    private JPanel fileSelectionPanel;
    private JButton btnSelectFile;
    private JLabel lblSelectedFile;

    private JPanel textInputPanel;
    private JTextArea txtInputText;
    private JScrollPane txtScroll;

    private JTextField txtKey;
    private JButton btnGenerateKey;

    private JButton btnExecute;

    private JPanel resultPanel;
    private JLabel lblResultTitle;
    private JTextArea txtOutputResult;
    private JScrollPane scrollResult;
    private JButton btnCopyResult;
    private JButton btnOpenFile;

    private File selectedInputFile = null;
    private File selectedOutputFile = null;

    public MainWindow() {
        super("Algoritmo AES");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 360));

        JPanel mainContainer = new JPanel();
        mainContainer.setBackground(COLOR_BG);
        mainContainer.setLayout(new BorderLayout(0, 8));
        mainContainer.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel actionPanel = createActionSection();
        contentPanel.add(actionPanel);
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel operationModePanel = createOperationModeSection();
        contentPanel.add(operationModePanel);
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel inputTypePanel = createInputTypeSection();
        contentPanel.add(inputTypePanel);
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel keyPanel = createKeySection();
        contentPanel.add(keyPanel);
        contentPanel.add(Box.createVerticalStrut(8));

        JPanel executePanel = createExecuteSection();
        contentPanel.add(executePanel);
        contentPanel.add(Box.createVerticalStrut(8));

        resultPanel = createResultSection();
        contentPanel.add(resultPanel);

        contentPanel.add(Box.createVerticalGlue());

        mainContainer.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);

        updateState();
        pack();
        setSize(660, getHeight());
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Algoritmo AES");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_TEXT_MAIN);

        panel.add(lblTitle);
        return panel;
    }

    private JPanel createCardWrapper() {
        JPanel card = new JPanel() {
            @Override
            public Dimension getMinimumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(0, pref.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }
        };
        card.setBackground(COLOR_CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_CARD_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(COLOR_TEXT_MAIN);
        lbl.setPreferredSize(new Dimension(SECTION_LABEL_WIDTH, 26));
        lbl.setMinimumSize(new Dimension(SECTION_LABEL_WIDTH, 26));
        lbl.setMaximumSize(new Dimension(SECTION_LABEL_WIDTH, 26));
        return lbl;
    }

    private JPanel createActionSection() {
        JPanel card = createCardWrapper();

        JLabel lblSection = createSectionLabel("Ação");

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setOpaque(false);

        rbEncrypt = new JRadioButton("Criptografar", true);
        rbDecrypt = new JRadioButton("Descriptografar", false);

        styleRadioButton(rbEncrypt);
        styleRadioButton(rbDecrypt);

        ButtonGroup bgAction = new ButtonGroup();
        bgAction.add(rbEncrypt);
        bgAction.add(rbDecrypt);

        rbEncrypt.addActionListener(e -> updateState());
        rbDecrypt.addActionListener(e -> updateState());

        rowPanel.add(lblSection);
        rowPanel.add(rbEncrypt);
        rowPanel.add(rbDecrypt);

        card.add(rowPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createOperationModeSection() {
        JPanel card = createCardWrapper();

        JLabel lblSection = createSectionLabel("Modo de operação");

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setOpaque(false);

        rbECB = new JRadioButton("ECB", true);
        rbCBC = new JRadioButton("CBC", false);

        styleRadioButton(rbECB);
        styleRadioButton(rbCBC);

        ButtonGroup bgMode = new ButtonGroup();
        bgMode.add(rbECB);
        bgMode.add(rbCBC);

        initializationVectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        initializationVectorPanel.setOpaque(false);

        JLabel lblIV = new JLabel("Vetor IV:");
        lblIV.setFont(FONT_BOLD);
        lblIV.setForeground(COLOR_TEXT_MAIN);

        txtIV = new JTextField(14);
        styleTextField(txtIV);
        txtIV.setToolTipText("16 bytes: 16 caracteres, 32 hexadecimais ou 16 números separados por vírgula");

        btnGenerateIV = new JButton("Gerar IV");
        styleStandardButton(btnGenerateIV);
        btnGenerateIV.addActionListener(e -> txtIV.setText(KeyHelper.generateRandomKeyOrIV()));

        initializationVectorPanel.add(lblIV);
        initializationVectorPanel.add(txtIV);
        initializationVectorPanel.add(btnGenerateIV);
        initializationVectorPanel.setVisible(false);

        rbECB.addActionListener(e -> updateState());
        rbCBC.addActionListener(e -> updateState());

        rowPanel.add(lblSection);
        rowPanel.add(rbECB);
        rowPanel.add(rbCBC);
        rowPanel.add(initializationVectorPanel);

        card.add(rowPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createInputTypeSection() {
        JPanel card = createCardWrapper();

        JLabel lblSection = createSectionLabel("Tipo de entrada");

        JPanel rowRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowRadio.setOpaque(false);
        rowRadio.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbText = new JRadioButton("Texto", true);
        rbFile = new JRadioButton("Arquivo", false);

        styleRadioButton(rbText);
        styleRadioButton(rbFile);

        ButtonGroup bgType = new ButtonGroup();
        bgType.add(rbText);
        bgType.add(rbFile);

        rowRadio.add(lblSection);
        rowRadio.add(rbText);
        rowRadio.add(rbFile);

        fileSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        fileSelectionPanel.setOpaque(false);
        fileSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileSelectionPanel.setBorder(new EmptyBorder(4, 0, 2, 0));

        btnSelectFile = new JButton("Escolher arquivo");
        styleStandardButton(btnSelectFile);
        btnSelectFile.addActionListener(e -> handleChooseInputFile());

        lblSelectedFile = new JLabel("Nenhum arquivo selecionado");
        lblSelectedFile.setFont(FONT_REGULAR);
        lblSelectedFile.setForeground(COLOR_TEXT_MUTED);

        fileSelectionPanel.add(btnSelectFile);
        fileSelectionPanel.add(lblSelectedFile);
        fileSelectionPanel.setVisible(false);

        textInputPanel = new JPanel(new BorderLayout(4, 4));
        textInputPanel.setOpaque(false);
        textInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textInputPanel.setBorder(new EmptyBorder(4, 0, 2, 0));

        JLabel lblTextDesc = new JLabel("Texto a ser processado:");
        lblTextDesc.setFont(FONT_REGULAR);
        lblTextDesc.setForeground(COLOR_TEXT_MAIN);

        txtInputText = new JTextArea(3, 30);
        txtInputText.setFont(FONT_MONO);
        txtInputText.setLineWrap(true);
        txtInputText.setWrapStyleWord(false);
        txtInputText.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_FIELD_BORDER, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
        txtInputText.setBackground(COLOR_FIELD_BG);

        txtScroll = new JScrollPane(txtInputText);
        txtScroll.setBorder(null);
        txtScroll.setMinimumSize(new Dimension(100, 58));
        txtScroll.setPreferredSize(new Dimension(100, 58));
        txtScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        txtScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        textInputPanel.add(lblTextDesc, BorderLayout.NORTH);
        textInputPanel.add(txtScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(rowRadio);
        centerPanel.add(fileSelectionPanel);
        centerPanel.add(textInputPanel);

        rbText.addActionListener(e -> updateState());
        rbFile.addActionListener(e -> updateState());

        card.add(centerPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createKeySection() {
        JPanel card = createCardWrapper();

        JLabel lblSection = new JLabel("Chave (16 bytes / 128 bits)");
        lblSection.setFont(FONT_SECTION);
        lblSection.setForeground(COLOR_TEXT_MAIN);

        JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(new EmptyBorder(4, 0, 2, 0));

        txtKey = new JTextField();
        styleTextField(txtKey);
        txtKey.setToolTipText("Aceita 16 caracteres, 32 hexadecimais ou 16 números entre 0 e 255 separados por vírgula");

        btnGenerateKey = new JButton("Gerar Chave");
        styleStandardButton(btnGenerateKey);
        btnGenerateKey.addActionListener(e -> txtKey.setText(KeyHelper.generateRandomKeyOrIV()));

        rowPanel.add(txtKey, BorderLayout.CENTER);
        rowPanel.add(btnGenerateKey, BorderLayout.EAST);

        card.add(lblSection, BorderLayout.NORTH);
        card.add(rowPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createExecuteSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnExecute = new JButton("Executar");
        styleStandardButton(btnExecute);
        btnExecute.addActionListener(e -> executeAES());

        panel.add(btnExecute);
        return panel;
    }

    private JPanel createResultSection() {
        JPanel card = createCardWrapper();
        card.setVisible(false);

        lblResultTitle = new JLabel("Resultado:");
        lblResultTitle.setFont(FONT_SECTION);
        lblResultTitle.setForeground(COLOR_TEXT_MAIN);

        txtOutputResult = new JTextArea(4, 30);
        txtOutputResult.setFont(FONT_MONO);
        txtOutputResult.setLineWrap(true);
        txtOutputResult.setWrapStyleWord(false);
        txtOutputResult.setEditable(false);
        txtOutputResult.setBackground(COLOR_FIELD_BG);
        txtOutputResult.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_FIELD_BORDER, 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));

        scrollResult = new JScrollPane(txtOutputResult);
        scrollResult.setBorder(null);
        scrollResult.setMinimumSize(new Dimension(100, 68));
        scrollResult.setPreferredSize(new Dimension(100, 68));
        scrollResult.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResult.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionsPanel.setOpaque(false);

        btnCopyResult = new JButton("Copiar Resultado");
        styleStandardButton(btnCopyResult);
        btnCopyResult.addActionListener(e -> {
            String text = txtOutputResult.getText();
            if (!text.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                JOptionPane.showMessageDialog(this, "Resultado copiado para a área de transferência!", "Copiado", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnOpenFile = new JButton("Abrir Pasta do Arquivo");
        styleStandardButton(btnOpenFile);
        btnOpenFile.setVisible(false);
        btnOpenFile.addActionListener(e -> {
            if (selectedOutputFile != null && selectedOutputFile.exists()) {
                try {
                    Desktop.getDesktop().open(selectedOutputFile.getParentFile());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Não foi possível abrir a pasta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionsPanel.add(btnOpenFile);
        actionsPanel.add(btnCopyResult);

        card.add(lblResultTitle, BorderLayout.NORTH);
        card.add(scrollResult, BorderLayout.CENTER);
        card.add(actionsPanel, BorderLayout.SOUTH);
        return card;
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setFont(FONT_REGULAR);
        rb.setForeground(COLOR_TEXT_MAIN);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(FONT_MONO);
        tf.setBackground(COLOR_FIELD_BG);
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_FIELD_BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleStandardButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(COLOR_BTN_BG);
        btn.setForeground(COLOR_TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BTN_BORDER, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_BTN_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_BTN_BG);
            }
        });
    }

    private void updateState() {
        boolean isCBC = rbCBC.isSelected();
        initializationVectorPanel.setVisible(isCBC);

        boolean isFile = rbFile.isSelected();
        fileSelectionPanel.setVisible(isFile);
        textInputPanel.setVisible(!isFile);

        boolean isEncrypt = rbEncrypt.isSelected();
        btnExecute.setText(isEncrypt ? "Criptografar" : "Descriptografar");

        if (isEncrypt) {
            txtInputText.setToolTipText("Digite o texto legível a ser criptografado");
        } else {
            txtInputText.setToolTipText("Digite ou cole o texto cifrado em hexadecimal");
        }

        revalidate();
        repaint();
        if (isDisplayable()) {
            adjustWindowSize();
        }
    }

    private void adjustWindowSize() {
        int width = Math.max(getWidth(), 660);
        pack();
        setSize(width, getHeight());
    }

    private String formatFileName(File file) {
        if (file == null) return "Nenhum arquivo selecionado";
        String name = file.getName();
        long sizeKb = file.length() / 1024;
        String sizeStr = " (" + sizeKb + " KB)";

        int maxNameLen = 32;
        if (name.length() > maxNameLen) {
            int cutStart = 18;
            int cutEnd = name.length() - 10;
            name = name.substring(0, cutStart) + "..." + name.substring(cutEnd);
        }
        return name + sizeStr;
    }

    private void handleChooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione o arquivo de entrada");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedInputFile = chooser.getSelectedFile();
            lblSelectedFile.setText(formatFileName(selectedInputFile));
            lblSelectedFile.setToolTipText(selectedInputFile.getAbsolutePath());
            lblSelectedFile.setForeground(COLOR_TEXT_MAIN);
        }
    }

    private void executeAES() {
        try {
            boolean isEncrypt = rbEncrypt.isSelected();
            boolean isCBC = rbCBC.isSelected();
            boolean isFile = rbFile.isSelected();

            byte[] key = KeyHelper.parseKeyOrIV(txtKey.getText(), "A Chave");

            byte[] iv = null;
            if(isCBC) {
                iv = KeyHelper.parseKeyOrIV(txtIV.getText(), "O Vetor IV");
            }

            File outputFile = null;
            if(isFile) {
                if(selectedInputFile == null || !selectedInputFile.exists()) {
                    throw new IllegalArgumentException("Por favor, selecione um arquivo de entrada.");
                }
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Salvar arquivo de saída como");
                String defaultName = selectedInputFile.getName();
                if(isEncrypt) {
                    defaultName = defaultName + ".enc";
                } else {
                    if(defaultName.endsWith(".enc")) {
                        defaultName = defaultName.substring(0, defaultName.length() - 4);
                    } else {
                        defaultName = "decrypted_" + defaultName;
                    }
                }
                saveChooser.setSelectedFile(new File(selectedInputFile.getParentFile(), defaultName));
                int saveRes = saveChooser.showSaveDialog(this);
                if(saveRes != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                outputFile = saveChooser.getSelectedFile();
                if(selectedInputFile.getCanonicalFile().equals(outputFile.getCanonicalFile())) {
                    throw new IllegalArgumentException("O arquivo de saída não pode ser o mesmo da entrada.");
                }
            }

            CypherData cypherData = new CypherData();
            cypherData.setOperation(isEncrypt ? "encrypt" : "decrypt");
            cypherData.setOperationMode(isCBC ? "CBC" : "ECB");
            cypherData.setFinalObjectType(isFile ? "file" : "text");
            cypherData.setKey(key);
            cypherData.setInitializationVector(iv);
            cypherData.setOutputFile(outputFile);

            if(isFile) {
                cypherData.setInputFile(selectedInputFile);
            } else {
                String rawText = txtInputText.getText();
                byte[] inputBytes = KeyHelper.parseInputText(rawText, !isEncrypt);
                cypherData.setBytedText(inputBytes);
            }

            lblResultTitle.setText("Processando... aguarde");
            resultPanel.setVisible(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnExecute.setEnabled(false);
            ((JPanel) getContentPane()).paintImmediately(getContentPane().getBounds());

            long startTime = System.currentTimeMillis();
            try {
                AES aes = new AES();
                if(isFile) {
                    if(isEncrypt) {
                        aes.start(cypherData);
                    } else {
                        aes.start(cypherData);
                    }
                    long elapsed = System.currentTimeMillis() - startTime;
                    selectedOutputFile = outputFile;
                    lblResultTitle.setText("Arquivo processado com sucesso! (" + elapsed + "ms)");
                    txtOutputResult.setText("Arquivo salvo com sucesso em:\n" + outputFile.getAbsolutePath() +
                            "\n\nTamanho do arquivo gerado: " + (outputFile.length() / 1024) + " KB (" + outputFile.length() + " bytes)");
                    btnOpenFile.setVisible(true);
                    btnCopyResult.setText("Copiar Caminho");
                } else {
                    byte[] resultBytes = aes.execute(cypherData);
                    long elapsed = System.currentTimeMillis() - startTime;
                    btnOpenFile.setVisible(false);
                    btnCopyResult.setText("Copiar Resultado");
                    if(isEncrypt) {
                        String hex = AES.toStringTextInHex(resultBytes);
                        lblResultTitle.setText("Texto Criptografado (" + elapsed + "ms, " + resultBytes.length + " bytes):");
                        txtOutputResult.setText(hex);
                    } else {
                        String plainText = new String(resultBytes, StandardCharsets.UTF_8);
                        lblResultTitle.setText("Texto Descriptografado (" + elapsed + "ms):");
                        txtOutputResult.setText(plainText);
                    }
                }
            } finally {
                setCursor(Cursor.getDefaultCursor());
                btnExecute.setEnabled(true);
            }

            revalidate();
            repaint();
            adjustWindowSize();

        } catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso de Validação", JOptionPane.WARNING_MESSAGE);
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro durante o processamento:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
