import * as React from 'react';

import { StyleSheet, ScrollView, Text, Image } from 'react-native';
import PngProcessor from 'react-native-png-processor';

export default function App() {
  const picture = Image.resolveAssetSource(require('./eit-floor.png'));
  console.log('source picture', picture);
  const [src] = React.useState<string>(picture.uri);
  const [res, setRes] = React.useState<string>(null);

  PngProcessor.makeTransparent(picture.uri)
    .then((result) => {
      console.log('result', result);
      setRes(result);
      //const size = Image.getSize(result);
      const img = Image.resolveAssetSource({ uri: result });
      console.log('img', img);
    })
    .catch((e) => {
      console.error('something bad happened', e);
    });
  const Header = (props) => <Text style={styles.header}>{props.children}</Text>;

  return (
    <ScrollView style={styles.container}>
      <Text>Uri: {src}</Text>
      <Header>Original</Header>
      <Image style={styles.box} source={{ uri: src }} resizeMode={'contain'} />
      <Header>Transformed</Header>
      <Image
        style={{
          ...styles.box
        }}
        source={{ uri: res }}
        resizeMode={'contain'}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'tomato',
  },
  header: {
    fontSize: 24,
    marginBottom: 15,
  },
  box: {
    width: '100%',
    height: 400,
  },
});
