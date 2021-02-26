import { NativeModules } from 'react-native';

type PngProcessorType = {
  multiply(a: number, b: number): Promise<number>;
};

const { PngProcessor } = NativeModules;

export default PngProcessor as PngProcessorType;
