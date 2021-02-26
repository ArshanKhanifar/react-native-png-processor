import { NativeModules } from 'react-native';

type PngProcessorType = {
  makeTransparent(image_uri: string): Promise<string>;
};

const { PngProcessor } = NativeModules;

export default PngProcessor as PngProcessorType;
