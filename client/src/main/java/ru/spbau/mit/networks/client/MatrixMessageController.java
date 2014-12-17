package ru.spbau.mit.networks.client;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.spbau.mit.networks.client.MatrixProtobufMessage.Matrix;

import java.util.ArrayList;
import java.util.Random;


public class MatrixMessageController implements MessageController {
    private final int matrixSize;
    private final Random random = new Random();
    private byte[] clientData = null;

    public MatrixMessageController(int matrixSize) {
        this.matrixSize = matrixSize;
    }

    @Override
    public byte[] createRequest() {
        if (clientData == null) {
            createMatrix();
        }
        return clientData;
    }

    public void createMatrix() {
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < matrixSize * matrixSize; i++) {
            data.add(random.nextInt(100));
        }

        clientData = Matrix.newBuilder()
                .addAllData(data)
                .setRows(matrixSize)
                .build().toByteArray();
    }

    @Override
    public void checkServerResponse(byte[] serverMessage) {
        try {
            Jama.Matrix serverMatrix = getMatrix(serverMessage);
            Jama.Matrix clientMatrix = getMatrix(clientData);

            System.out.println(almostIdentity(serverMatrix.inverse().times(clientMatrix).minus(Jama.Matrix.identity(matrixSize, matrixSize))));

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private boolean almostIdentity(Jama.Matrix a) {
        final double epsilon = 1e-8;
        int rows = a.getRowDimension();
        int columns = a.getColumnDimension();
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                double ea = a.get(r, c);
                if (Math.abs(ea) >= epsilon) {
                    return false;
                }
            }
        }
        return true;
    }

    private Jama.Matrix getMatrix(byte[] data) throws InvalidProtocolBufferException {
        Matrix matrix = Matrix.parseFrom(data);
        return new Jama.Matrix(matrix.getDataList().stream().mapToDouble(Double::valueOf).toArray(), matrix.getRows());
    }
}